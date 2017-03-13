
package com.byd.runin;

import android.os.Parcel;
import android.os.Parcelable;

public class TestListItem implements Parcelable
{
    String testMode;
    String testItemName;
    String testItemValue;
    String testItemUnit;

    public TestListItem(){

    }

    public TestListItem(Parcel in)
    {
        testMode = in.readString();
        testItemName = in.readString();
        testItemValue = in.readString();
        testItemUnit = in.readString();
    }

    public String getTestMode()
    {
        return testMode;
    }

    public void setTestmode(String testMode)
    {
        this.testMode = testMode;
    }

    public String getTestItemName()
    {
        return testItemName;
    }

    public void setTestItemName(String testItemName)
    {
        this.testItemName = testItemName;
    }

    public String getTestItemValue()
    {
        return testItemValue;
    }

    public void setTestItemValue(String testItemValue)
    {
        this.testItemValue = testItemValue;
    }

    public String getTestItemUnit()
    {
        return testItemUnit;
    }

    public void setTestItemUnit(String testItemUnit)
    {
        this.testItemUnit = testItemUnit;
    }
    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        // TODO Auto-generated method stub
        out.writeString(testMode);
        out.writeString(testItemName);
        out.writeString(testItemValue);
        out.writeString(testItemUnit);

    }

    public static final Parcelable.Creator < TestListItem > CREATOR = new
        Parcelable.Creator < TestListItem > ()
    {

        @Override
        public TestListItem createFromParcel(Parcel source)
        {
            // TODO Auto-generated method stub
            return new TestListItem(source);
        }

        @Override
        public TestListItem[]newArray(int size)
        {
            // TODO Auto-generated method stub
            return new TestListItem[size];
        }

    };
}
