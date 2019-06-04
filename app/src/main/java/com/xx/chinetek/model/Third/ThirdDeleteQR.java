package com.xx.chinetek.model.Third;

/**
 * Created by GHOST on 2018/12/29.
 */

public class ThirdDeleteQR {

    private Integer RowNo;
    private String GolfaCode;
    private String SerialNo;
    private String VoucherNo;
    private String Type;

    public Integer getRowNo() {
        return RowNo;
    }

    public void setRowNo(Integer rowNo) {
        RowNo = rowNo;
    }

    public String getGolfaCode() {
        return GolfaCode;
    }

    public void setGolfaCode(String golfaCode) {
        GolfaCode = golfaCode;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThirdDeleteQR)) return false;

        ThirdDeleteQR that = (ThirdDeleteQR) o;

        if (RowNo != null ? !RowNo.equals(that.RowNo) : that.RowNo != null) return false;
        return Type != null ? Type.equals(that.Type) : that.Type == null;
    }

    @Override
    public int hashCode() {
        int result = RowNo != null ? RowNo.hashCode() : 0;
        result = 31 * result + (Type != null ? Type.hashCode() : 0);
        return result;
    }
}
