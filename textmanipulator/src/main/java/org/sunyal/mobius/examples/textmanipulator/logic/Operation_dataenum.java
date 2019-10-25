package org.sunyal.mobius.examples.textmanipulator.logic;

import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface Operation_dataenum {
    dataenum_case Reverse();

    dataenum_case Uppercase();

    dataenum_case Lowercase();

    dataenum_case None();
}
