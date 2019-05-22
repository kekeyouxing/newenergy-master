//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.binarywang.wxpay.bean.notify;

import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

@XStreamAlias("xml")
public class WxPayRefundNotifyResult extends BaseWxPayResult implements Serializable {
    private static final long serialVersionUID = 4651725860079259186L;
    @XStreamAlias("req_info")
    private String reqInfoString;
    private WxPayRefundNotifyResult.ReqInfo reqInfo;

    public static WxPayRefundNotifyResult fromXML(String xmlString, String mchKey) throws WxPayException {
        WxPayRefundNotifyResult result = (WxPayRefundNotifyResult)BaseWxPayResult.fromXML(xmlString, WxPayRefundNotifyResult.class);
        if ("FAIL".equals(result.getReturnCode())) {
            return result;
        } else {
            String reqInfoString = result.getReqInfoString();

            try {
                String keyMd5String = DigestUtils.md5Hex(mchKey).toLowerCase();
                SecretKeySpec key = new SecretKeySpec(keyMd5String.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(2, key);
                result.setReqInfo(WxPayRefundNotifyResult.ReqInfo.fromXML(new String(cipher.doFinal(Base64.decodeBase64(reqInfoString)), StandardCharsets.UTF_8)));
                return result;
            } catch (Exception var7) {
                throw new WxPayException("解密退款通知加密信息时出错", var7);
            }
        }
    }

    public String getReqInfoString() {
        return this.reqInfoString;
    }

    public WxPayRefundNotifyResult.ReqInfo getReqInfo() {
        return this.reqInfo;
    }

    public void setReqInfoString(String reqInfoString) {
        this.reqInfoString = reqInfoString;
    }

    public void setReqInfo(WxPayRefundNotifyResult.ReqInfo reqInfo) {
        this.reqInfo = reqInfo;
    }

    public String toString() {
        return "WxPayRefundNotifyResult(reqInfoString=" + this.getReqInfoString() + ", reqInfo=" + this.getReqInfo() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WxPayRefundNotifyResult)) {
            return false;
        } else {
            WxPayRefundNotifyResult other = (WxPayRefundNotifyResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$reqInfoString = this.getReqInfoString();
                Object other$reqInfoString = other.getReqInfoString();
                if (this$reqInfoString == null) {
                    if (other$reqInfoString != null) {
                        return false;
                    }
                } else if (!this$reqInfoString.equals(other$reqInfoString)) {
                    return false;
                }

                Object this$reqInfo = this.getReqInfo();
                Object other$reqInfo = other.getReqInfo();
                if (this$reqInfo == null) {
                    if (other$reqInfo != null) {
                        return false;
                    }
                } else if (!this$reqInfo.equals(other$reqInfo)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WxPayRefundNotifyResult;
    }

    public int hashCode() {
//        int PRIME = true;
        int result = 1;
        result = result * 59 + super.hashCode();
        Object $reqInfoString = this.getReqInfoString();
        result = result * 59 + ($reqInfoString == null ? 43 : $reqInfoString.hashCode());
        Object $reqInfo = this.getReqInfo();
        result = result * 59 + ($reqInfo == null ? 43 : $reqInfo.hashCode());
        return result;
    }

    public WxPayRefundNotifyResult() {
    }

    @ConstructorProperties({"reqInfoString", "reqInfo"})
    public WxPayRefundNotifyResult(String reqInfoString, WxPayRefundNotifyResult.ReqInfo reqInfo) {
        this.reqInfoString = reqInfoString;
        this.reqInfo = reqInfo;
    }

    @XStreamAlias("root")
    public static class ReqInfo {
        @XStreamAlias("transaction_id")
        private String transactionId;
        @XStreamAlias("out_trade_no")
        private String outTradeNo;
        @XStreamAlias("refund_id")
        private String refundId;
        @XStreamAlias("out_refund_no")
        private String outRefundNo;
        @XStreamAlias("total_fee")
        private Integer totalFee;
        @XStreamAlias("settlement_total_fee")
        private Integer settlementTotalFee;
        @XStreamAlias("refund_fee")
        private Integer refundFee;
        @XStreamAlias("settlement_refund_fee")
        private Integer settlementRefundFee;
        @XStreamAlias("refund_status")
        private String refundStatus;
        @XStreamAlias("success_time")
        private String successTime;
        @XStreamAlias("refund_recv_accout")
        private String refundRecvAccout;
        @XStreamAlias("refund_account")
        private String refundAccount;
        @XStreamAlias("refund_request_source")
        private String refundRequestSource;

        public String toString() {
            return WxGsonBuilder.create().toJson(this);
        }

        public static WxPayRefundNotifyResult.ReqInfo fromXML(String xmlString) {
            XStream xstream = XStreamInitializer.getInstance();
            xstream.processAnnotations(WxPayRefundNotifyResult.ReqInfo.class);
            return (WxPayRefundNotifyResult.ReqInfo)xstream.fromXML(xmlString);
        }

        public String getTransactionId() {
            return this.transactionId;
        }

        public String getOutTradeNo() {
            return this.outTradeNo;
        }

        public String getRefundId() {
            return this.refundId;
        }

        public String getOutRefundNo() {
            return this.outRefundNo;
        }

        public Integer getTotalFee() {
            return this.totalFee;
        }

        public Integer getSettlementTotalFee() {
            return this.settlementTotalFee;
        }

        public Integer getRefundFee() {
            return this.refundFee;
        }

        public Integer getSettlementRefundFee() {
            return this.settlementRefundFee;
        }

        public String getRefundStatus() {
            return this.refundStatus;
        }

        public String getSuccessTime() {
            return this.successTime;
        }

        public String getRefundRecvAccout() {
            return this.refundRecvAccout;
        }

        public String getRefundAccount() {
            return this.refundAccount;
        }

        public String getRefundRequestSource() {
            return this.refundRequestSource;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }

        public void setOutRefundNo(String outRefundNo) {
            this.outRefundNo = outRefundNo;
        }

        public void setTotalFee(Integer totalFee) {
            this.totalFee = totalFee;
        }

        public void setSettlementTotalFee(Integer settlementTotalFee) {
            this.settlementTotalFee = settlementTotalFee;
        }

        public void setRefundFee(Integer refundFee) {
            this.refundFee = refundFee;
        }

        public void setSettlementRefundFee(Integer settlementRefundFee) {
            this.settlementRefundFee = settlementRefundFee;
        }

        public void setRefundStatus(String refundStatus) {
            this.refundStatus = refundStatus;
        }

        public void setSuccessTime(String successTime) {
            this.successTime = successTime;
        }

        public void setRefundRecvAccout(String refundRecvAccout) {
            this.refundRecvAccout = refundRecvAccout;
        }

        public void setRefundAccount(String refundAccount) {
            this.refundAccount = refundAccount;
        }

        public void setRefundRequestSource(String refundRequestSource) {
            this.refundRequestSource = refundRequestSource;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof WxPayRefundNotifyResult.ReqInfo)) {
                return false;
            } else {
                WxPayRefundNotifyResult.ReqInfo other = (WxPayRefundNotifyResult.ReqInfo)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    label167: {
                        Object this$transactionId = this.getTransactionId();
                        Object other$transactionId = other.getTransactionId();
                        if (this$transactionId == null) {
                            if (other$transactionId == null) {
                                break label167;
                            }
                        } else if (this$transactionId.equals(other$transactionId)) {
                            break label167;
                        }

                        return false;
                    }

                    Object this$outTradeNo = this.getOutTradeNo();
                    Object other$outTradeNo = other.getOutTradeNo();
                    if (this$outTradeNo == null) {
                        if (other$outTradeNo != null) {
                            return false;
                        }
                    } else if (!this$outTradeNo.equals(other$outTradeNo)) {
                        return false;
                    }

                    label153: {
                        Object this$refundId = this.getRefundId();
                        Object other$refundId = other.getRefundId();
                        if (this$refundId == null) {
                            if (other$refundId == null) {
                                break label153;
                            }
                        } else if (this$refundId.equals(other$refundId)) {
                            break label153;
                        }

                        return false;
                    }

                    Object this$outRefundNo = this.getOutRefundNo();
                    Object other$outRefundNo = other.getOutRefundNo();
                    if (this$outRefundNo == null) {
                        if (other$outRefundNo != null) {
                            return false;
                        }
                    } else if (!this$outRefundNo.equals(other$outRefundNo)) {
                        return false;
                    }

                    label139: {
                        Object this$totalFee = this.getTotalFee();
                        Object other$totalFee = other.getTotalFee();
                        if (this$totalFee == null) {
                            if (other$totalFee == null) {
                                break label139;
                            }
                        } else if (this$totalFee.equals(other$totalFee)) {
                            break label139;
                        }

                        return false;
                    }

                    Object this$settlementTotalFee = this.getSettlementTotalFee();
                    Object other$settlementTotalFee = other.getSettlementTotalFee();
                    if (this$settlementTotalFee == null) {
                        if (other$settlementTotalFee != null) {
                            return false;
                        }
                    } else if (!this$settlementTotalFee.equals(other$settlementTotalFee)) {
                        return false;
                    }

                    label125: {
                        Object this$refundFee = this.getRefundFee();
                        Object other$refundFee = other.getRefundFee();
                        if (this$refundFee == null) {
                            if (other$refundFee == null) {
                                break label125;
                            }
                        } else if (this$refundFee.equals(other$refundFee)) {
                            break label125;
                        }

                        return false;
                    }

                    label118: {
                        Object this$settlementRefundFee = this.getSettlementRefundFee();
                        Object other$settlementRefundFee = other.getSettlementRefundFee();
                        if (this$settlementRefundFee == null) {
                            if (other$settlementRefundFee == null) {
                                break label118;
                            }
                        } else if (this$settlementRefundFee.equals(other$settlementRefundFee)) {
                            break label118;
                        }

                        return false;
                    }

                    Object this$refundStatus = this.getRefundStatus();
                    Object other$refundStatus = other.getRefundStatus();
                    if (this$refundStatus == null) {
                        if (other$refundStatus != null) {
                            return false;
                        }
                    } else if (!this$refundStatus.equals(other$refundStatus)) {
                        return false;
                    }

                    label104: {
                        Object this$successTime = this.getSuccessTime();
                        Object other$successTime = other.getSuccessTime();
                        if (this$successTime == null) {
                            if (other$successTime == null) {
                                break label104;
                            }
                        } else if (this$successTime.equals(other$successTime)) {
                            break label104;
                        }

                        return false;
                    }

                    label97: {
                        Object this$refundRecvAccout = this.getRefundRecvAccout();
                        Object other$refundRecvAccout = other.getRefundRecvAccout();
                        if (this$refundRecvAccout == null) {
                            if (other$refundRecvAccout == null) {
                                break label97;
                            }
                        } else if (this$refundRecvAccout.equals(other$refundRecvAccout)) {
                            break label97;
                        }

                        return false;
                    }

                    Object this$refundAccount = this.getRefundAccount();
                    Object other$refundAccount = other.getRefundAccount();
                    if (this$refundAccount == null) {
                        if (other$refundAccount != null) {
                            return false;
                        }
                    } else if (!this$refundAccount.equals(other$refundAccount)) {
                        return false;
                    }

                    Object this$refundRequestSource = this.getRefundRequestSource();
                    Object other$refundRequestSource = other.getRefundRequestSource();
                    if (this$refundRequestSource == null) {
                        if (other$refundRequestSource != null) {
                            return false;
                        }
                    } else if (!this$refundRequestSource.equals(other$refundRequestSource)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof WxPayRefundNotifyResult.ReqInfo;
        }

        public int hashCode() {
//            int PRIME = true;
            int result = 1;
            Object $transactionId = this.getTransactionId();
            result = result * 59 + ($transactionId == null ? 43 : $transactionId.hashCode());
            Object $outTradeNo = this.getOutTradeNo();
            result = result * 59 + ($outTradeNo == null ? 43 : $outTradeNo.hashCode());
            Object $refundId = this.getRefundId();
            result = result * 59 + ($refundId == null ? 43 : $refundId.hashCode());
            Object $outRefundNo = this.getOutRefundNo();
            result = result * 59 + ($outRefundNo == null ? 43 : $outRefundNo.hashCode());
            Object $totalFee = this.getTotalFee();
            result = result * 59 + ($totalFee == null ? 43 : $totalFee.hashCode());
            Object $settlementTotalFee = this.getSettlementTotalFee();
            result = result * 59 + ($settlementTotalFee == null ? 43 : $settlementTotalFee.hashCode());
            Object $refundFee = this.getRefundFee();
            result = result * 59 + ($refundFee == null ? 43 : $refundFee.hashCode());
            Object $settlementRefundFee = this.getSettlementRefundFee();
            result = result * 59 + ($settlementRefundFee == null ? 43 : $settlementRefundFee.hashCode());
            Object $refundStatus = this.getRefundStatus();
            result = result * 59 + ($refundStatus == null ? 43 : $refundStatus.hashCode());
            Object $successTime = this.getSuccessTime();
            result = result * 59 + ($successTime == null ? 43 : $successTime.hashCode());
            Object $refundRecvAccout = this.getRefundRecvAccout();
            result = result * 59 + ($refundRecvAccout == null ? 43 : $refundRecvAccout.hashCode());
            Object $refundAccount = this.getRefundAccount();
            result = result * 59 + ($refundAccount == null ? 43 : $refundAccount.hashCode());
            Object $refundRequestSource = this.getRefundRequestSource();
            result = result * 59 + ($refundRequestSource == null ? 43 : $refundRequestSource.hashCode());
            return result;
        }

        public ReqInfo() {
        }
    }
}
