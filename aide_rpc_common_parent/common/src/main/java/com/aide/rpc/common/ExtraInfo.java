package com.aide.rpc.common;

import com.facebook.swift.codec.*;
import com.facebook.swift.codec.ThriftField.Requiredness;
import java.util.*;

import static com.google.common.base.Objects.toStringHelper;

@ThriftStruct("ExtraInfo")
public final class ExtraInfo
{
    public ExtraInfo() {
    }

    private Map<String, String> strMap;

    @ThriftField(value=1, name="strMap", requiredness=Requiredness.NONE)
    public Map<String, String> getStrMap() { return strMap; }

    @ThriftField
    public void setStrMap(final Map<String, String> strMap) { this.strMap = strMap; }

    private Map<String, Long> numMap;

    @ThriftField(value=2, name="numMap", requiredness=Requiredness.NONE)
    public Map<String, Long> getNumMap() { return numMap; }

    @ThriftField
    public void setNumMap(final Map<String, Long> numMap) { this.numMap = numMap; }

    private Map<String, Double> floatMap;

    @ThriftField(value=3, name="floatMap", requiredness=Requiredness.NONE)
    public Map<String, Double> getFloatMap() { return floatMap; }

    @ThriftField
    public void setFloatMap(final Map<String, Double> floatMap) { this.floatMap = floatMap; }

    @Override
    public String toString()
    {
        return toStringHelper(this)
            .add("strMap", strMap)
            .add("numMap", numMap)
            .add("floatMap", floatMap)
            .toString();
    }
}
