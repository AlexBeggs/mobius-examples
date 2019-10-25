package org.sunyal.mobius.examples.textmanipulator.logic;

import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface TextManipulatorEvent_dataenum {

    dataenum_case TextChanged(String text);

    dataenum_case OperationChanged(Operation operation);

}
