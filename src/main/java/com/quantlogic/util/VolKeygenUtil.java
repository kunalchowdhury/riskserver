package com.quantlogic.util;

import com.quantlogic.annotation.RuleUtil;

@RuleUtil
public class VolKeygenUtil {
    public static String getFlatVolKey(String und, String comp, String index){
        return "FlatVol|"+und+"|"+comp+"|"+index;
    }
}
