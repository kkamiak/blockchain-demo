package by.instinctools.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.BigIntegers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by haria on 23.11.17.
 */
public class EncodedTransaction {
    private static final Logger logger = LoggerFactory.getLogger(EncodedTransaction.class);

    private byte[] hash;
    private byte[] nonce;
    private byte[] value;
    private byte[] receiveAddress;
    private byte[] gasPrice;
    private byte[] gasLimit;
    private byte[] data;
    protected byte[] sendAddress;
    protected byte[] rlpEncoded;
    private byte[] rlpRaw;
    protected boolean parsed;
    private Integer chainId;

    public EncodedTransaction(byte[] rawData) {
        this.rlpEncoded = rawData;
        parsed = false;
    }

    /* creation contract tx
     * [ nonce, gasPrice, gasLimit, "", endowment, init, signature(v, r, s) ]
     * or simple send tx
     * [ nonce, gasPrice, gasLimit, receiveAddress, value, data, signature(v, r, s) ]
     */
    public EncodedTransaction(byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.receiveAddress = receiveAddress;
        this.value = value;
        this.data = data;

        if(receiveAddress == null) {
            this.receiveAddress = ByteUtil.EMPTY_BYTE_ARRAY;
        }
        parsed = true;
    }

    public void rlpParse() {
        if(!this.parsed) {
            try {

                RlpList e = RlpDecoder.decode(this.rlpEncoded);

                RlpList transaction = (RlpList) e.getValues().get(0);
                if (transaction.getValues().size() > 9) {
                    throw new RuntimeException("Too many RLP elements");
                } else {
                    Iterator vData = transaction.getValues().iterator();

                    RlpType v;
                    do {
                        if (!vData.hasNext()) {
                            this.nonce = ((RlpString) transaction.getValues().get(0)).getBytes();
                            this.gasPrice = ((RlpString) transaction.getValues().get(1)).getBytes();
                            this.gasLimit = ((RlpString) transaction.getValues().get(2)).getBytes();
                            this.receiveAddress = ((RlpString) transaction.getValues().get(3)).getBytes();
                            this.value = ((RlpString) transaction.getValues().get(4)).getBytes();
                            this.data = ((RlpString) transaction.getValues().get(5)).getBytes();
                            if (((RlpString) transaction.getValues().get(6)).getBytes() != null) {
                                byte[] vData1 = ((RlpString) transaction.getValues().get(6)).getBytes();
                                BigInteger v1 = new BigInteger(vData1);
                                this.chainId = extractChainIdFromV(v1);
                                byte[] r = ((RlpString) transaction.getValues().get(7)).getBytes();
                                byte[] s = ((RlpString) transaction.getValues().get(8)).getBytes();
//                            this.signature = ECDSASignature.fromComponents(r, s, this.getRealV(v1));
                            } else {
//                            logger.debug("RLP encoded tx is not signed!");
                            }

                        }

                        v = (RlpString) vData.next();
                    } while (v instanceof RlpString);

                    throw new RuntimeException("Transaction RLP elements shouldn\'t be lists");
                }
            } catch (Exception var7) {
                throw new RuntimeException("Error on parsing RLP", var7);
            }
        }
    }

    private static Integer extractChainIdFromV(BigInteger bv) {
        if(bv.bitLength() > 31) {
            return Integer.valueOf(2147483647);
        } else {
            long v = bv.longValue();
            return v != 27L && v != 28L?Integer.valueOf((int)((v - 35L) / 2L)):null;
        }
    }
    public String toString() {
        return this.toString(2147483647);
    }

    public String toString(int maxDataSize) {
        this.rlpParse();
        String dataS;
        if(this.data == null) {
            dataS = "";
        } else if(this.data.length < maxDataSize) {
            dataS = ByteUtil.toHexString(this.data);
        } else {
            dataS = ByteUtil.toHexString(Arrays.copyOfRange(this.data, 0, maxDataSize)) + "... (" + this.data.length + " bytes)";
        }

        return "TransactionData [hash=" + ByteUtil.toHexString(this.hash) + "  nonce=" + ByteUtil.toHexString(this.nonce) +
                ", gasPrice=" + ByteUtil.toHexString(this.gasPrice) +
                ", gas=" + ByteUtil.toHexString(this.gasLimit) +
                ", receiveAddress=" + ByteUtil.toHexString(this.receiveAddress) + "]";
    }

    public byte[] getData() {
        this.rlpParse();
        return this.data;
    }

    public byte[] getGasLimit() {
        this.rlpParse();
        return this.gasLimit == null?ByteUtil.ZERO_BYTE_ARRAY:this.gasLimit;
    }

    public byte[] getGasPrice() {
        this.rlpParse();
        return this.gasPrice == null?ByteUtil.ZERO_BYTE_ARRAY:this.gasPrice;
    }

    public byte[] getReceiveAddress() {
        this.rlpParse();
        return this.receiveAddress;
    }



    public byte[] getValue() {
        this.rlpParse();
        return this.value == null?ByteUtil.ZERO_BYTE_ARRAY:this.value;
    }

    public boolean isParsed() {
        return this.parsed;
    }


    public byte[] getNonce() {
        this.rlpParse();
        return this.nonce == null?ByteUtil.ZERO_BYTE_ARRAY:this.nonce;
    }


}
