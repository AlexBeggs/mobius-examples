package org.sunyal.mobius.examples.textmanipulator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.mobius.MobiusLoop;

import org.apache.commons.lang3.StringUtils;
import org.sunyal.mobius.examples.textmanipulator.logic.EventHandler;
import org.sunyal.mobius.examples.textmanipulator.logic.Operation;
import org.sunyal.mobius.examples.textmanipulator.logic.TextManipulatorEvent;
import org.sunyal.mobius.examples.textmanipulator.logic.TextManipulatorModel;
import org.sunyal.mobius.examples.textmanipulator.view.TextManipulatorView;
import org.sunyal.mobius.examples.textmanipulator.view.TextManipulatorViewListener;

public class MainActivity extends AppCompatActivity implements TextManipulatorView {

    private TextManipulatorViewListener mViewListener;
    private EditText mEditText;
    private TextView mDisplayView;
    private RadioGroup mOperationGroup;
    private boolean disableChangeEvents = false;

    private MobiusLoop.Controller<TextManipulatorModel, TextManipulatorEvent> mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.text_value);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // no-op
            }

            @Override
            public void afterTextChanged(Editable editable) {
                final TextManipulatorViewListener listener = mViewListener;
                if (listener != null && !disableChangeEvents) {
                    listener.textChanged(editable.toString());
                }
            }
        });
        mDisplayView = findViewById(R.id.display_value);
        mOperationGroup = findViewById(R.id.operations);
        mOperationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                final TextManipulatorViewListener listener = mViewListener;
                if (listener != null && !disableChangeEvents) {
                    switch (id) {
                        case R.id.reverse_operation:
                            listener.operationChanged(Operation.reverse());
                            break;
                        case R.id.uppercase_operation:
                            listener.operationChanged(Operation.uppercase());
                            break;
                        case R.id.lowercase_operation:
                            listener.operationChanged(Operation.lowercase());
                            break;
                        case R.id.none_operation:
                            listener.operationChanged(Operation.none());
                            break;
                        default:
                            throw new IllegalStateException("Unknown id: " + id + " " + findViewById(id));
                    }
                }
            }
        });
        mController = EventHandler.controller(EventHandler.defaultModel());
        mController.connect(EventHandler.connectUi(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController.disconnect();
    }

    @Override
    public void bind(TextManipulatorModel model) {
        // allows us to initialize to a different value
        // prevent triggering another change event
        disableChangeEvents = true;
        if (!StringUtils.equals(mEditText.getText().toString(), model.textValue())) {
            int end = getCaret(mEditText, model);
            mEditText.setText(model.textValue());
            setCaret(mEditText, end);
        }
        View selectedOption = mOperationGroup.findViewById(mOperationGroup.getCheckedRadioButtonId());
        int index = mOperationGroup.indexOfChild(selectedOption);
        Operation operation = getOperation(index);
        if (!operation.equals(model.operation())) {
            mOperationGroup.check(getOperationId(model.operation()));
        }
        mDisplayView.setText(model.displayValue());
        disableChangeEvents = false;
    }

    private static int getCaret(EditText editText, TextManipulatorModel model) {
        return Math.min(editText.getSelectionEnd(), model.textValue().length());
    }

    private static void setCaret(EditText mEditText, int end) {
        mEditText.setSelection(end);
    }

    private static int getOperationId(Operation operation) {
        return operation.map(reverse -> R.id.reverse_operation,
                uppercase -> R.id.uppercase_operation,
                lowercase -> R.id.lowercase_operation,
                none -> R.id.none_operation
        );
    }

    private static Operation getOperation(int index) {
        switch (index) {
            case 0:
                return Operation.reverse();
            case 1:
                return Operation.uppercase();
            case 2:
                return Operation.lowercase();
            default:
                return Operation.none();
        }
    }

    @Override
    public void setViewListener(TextManipulatorViewListener viewListener) {
        mViewListener = viewListener;
    }

}
