package org.sunyal.mobius.examples.textmanipulator.logic;

import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.First;
import com.spotify.mobius.Mobius;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.Next;
import com.spotify.mobius.android.AndroidLogger;
import com.spotify.mobius.android.MobiusAndroid;
import com.spotify.mobius.functions.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.sunyal.mobius.examples.textmanipulator.view.TextManipulatorView;
import org.sunyal.mobius.examples.textmanipulator.view.TextManipulatorViewListener;

import javax.annotation.Nonnull;

public class EventHandler {

    public static Connectable<TextManipulatorModel, TextManipulatorEvent> connectUi(final TextManipulatorView view) {
        return new Connectable<TextManipulatorModel, TextManipulatorEvent>() {
            @Nonnull
            @Override
            public Connection<TextManipulatorModel> connect(final Consumer<TextManipulatorEvent> output) throws ConnectionLimitExceededException {
                // connect the SimpleView UI events to the Mobius Events output
                TextManipulatorViewListener viewListener = new TextManipulatorViewListener() {

                    @Override
                    public void textChanged(final String text) {
                        output.accept(TextManipulatorEvent.textChanged(text));
                    }

                    @Override
                    public void operationChanged(final Operation operation) {
                        output.accept(TextManipulatorEvent.operationChanged(operation));
                    }

                };
                view.setViewListener(viewListener);

                // return a Connection that receives updated TextManipulatorModels from the Mobius when
                // things change and then renders them in the TextManipulatorView with #bind
                return new Connection<TextManipulatorModel>() {
                    @Override
                    public void accept(final TextManipulatorModel model) {
                        view.bind(model);
                    }

                    @Override
                    public void dispose() {
                        view.setViewListener(null);
                    }
                };
            }
        };
    }

    public static MobiusLoop.Controller<TextManipulatorModel, TextManipulatorEvent> controller(TextManipulatorModel initModel) {
        return MobiusAndroid.controller(loop(), initModel);
    }

    public static TextManipulatorModel defaultModel() {
        return TextManipulatorModel.builder()
                .textValue("")
                .displayValue("")
                .operation(Operation.none())
                .build();
    }

    public static Connection<TextManipulatorEffect> noopEffectHandler(Consumer<TextManipulatorEvent> output) throws ConnectionLimitExceededException {
        return new Connection<TextManipulatorEffect>() {
            @Override
            public void accept(TextManipulatorEffect value) {
            }

            @Override
            public void dispose() {
            }
        };
    }

    private static MobiusLoop.Factory<TextManipulatorModel, TextManipulatorEvent, TextManipulatorEffect> loop() {
        return Mobius.loop(
                EventHandler::update,
                EventHandler::noopEffectHandler
        ).logger(AndroidLogger.tag("TextManipulator"));
    }

    public static First<TextManipulatorModel, TextManipulatorEffect> init(TextManipulatorModel model) {
        return First.first(model);
    }

    public static Next<TextManipulatorModel, TextManipulatorEffect> update(TextManipulatorModel model, TextManipulatorEvent event) {
        return event.map(textChanged -> {
                    String text = textChanged.text();
                    // apply operation
                    TextManipulatorModel textManipulatorModel = model.withTextAndDisplayValue(text, applyOperation(model.operation(), text));
                    return Next.next(textManipulatorModel);
                },
                operationChanged -> {
                    Operation operation = operationChanged.operation();
                    String displayValue = applyOperation(operation, model.textValue());
                    TextManipulatorModel textManipulatorModel = model.withOperation(operation, displayValue);
                    return Next.next(textManipulatorModel);
                }
        );
    }

    private static String applyOperation(Operation operation, String text) {
        return operation.map(
                reverse -> StringUtils.reverse(text),
                uppercase -> StringUtils.upperCase(text),
                lowercase -> StringUtils.lowerCase(text),
                none -> text
        );
    }

}
