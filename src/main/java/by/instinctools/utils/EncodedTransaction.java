package by.instinctools.utils;

import org.bouncycastle.util.BigIntegers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.ECDSASignature;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

import static by.instinctools.utils.ByteUtil.EMPTY_BYTE_ARRAY;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

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
    private ECDSASignature signature;

    public EncodedTransaction(byte[] rawData) {
        this.rlpEncoded = rawData;
        parsed = false;
    }

    public EncodedTransaction(byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data,
                       Integer chainId) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.receiveAddress = receiveAddress;
        if (ByteUtil.isSingleZero(value)) {
            this.value = EMPTY_BYTE_ARRAY;
        } else {
            this.value = value;
        }
        this.data = data;
        this.chainId = chainId;

        if (receiveAddress == null) {
            this.receiveAddress = EMPTY_BYTE_ARRAY;
        }

        parsed = true;
    }

    /**
     * Warning: this transaction would not be protected by replay-attack protection mechanism
     * Use {@link EncodedTransaction#EncodedTransaction(byte[], byte[], byte[], byte[], byte[], byte[], Integer)} constructor instead
     * and specify the desired chainID
     */
    public EncodedTransaction(byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data) {
        this(nonce, gasPrice, gasLimit, receiveAddress, value, data, null);
    }

    public EncodedTransaction(byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data,
                       byte[] r, byte[] s, byte v, Integer chainId) {
        this(nonce, gasPrice, gasLimit, receiveAddress, value, data, chainId);
    }


    public EncodedTransaction(byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data,
                       byte[] r, byte[] s, byte v) {
        this(nonce, gasPrice, gasLimit, receiveAddress, value, data, r, s, v, null);
    }

    public void rlpParse() {
        if (parsed) return;
        try {
            RlpList decodedTxList = RLP.decode(this.rlpEncoded);

            RlpList transaction = (RlpList) decodedTxList.getValues().get(0);
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
        } catch (Exception e) {
            throw new RuntimeException("Error on parsing RLP", e);
        }
    }

    private static Integer extractChainIdFromV(BigInteger bv) {
        if (bv.bitLength() > 31) {
            return Integer.valueOf(2147483647);
        } else {
            long v = bv.longValue();
            return v != 27L && v != 28L ? Integer.valueOf((int) ((v - 35L) / 2L)) : null;
        }
    }

    public byte[] getHash() {
        if (!isEmpty(hash)) return hash;

        rlpParse();
        byte[] plainMsg = this.getEncoded();
        return HashUtil.sha3(plainMsg);
    }

    public byte[] getRawHash() {
        rlpParse();
        byte[] plainMsg = this.getEncodedRaw();
        return HashUtil.sha3(plainMsg);
    }


    public byte[] getEncodedRaw() {

        rlpParse();
        if (rlpRaw != null) return rlpRaw;

        // parse null as 0 for nonce
        byte[] nonce = null;
        if (this.nonce == null || this.nonce.length == 1 && this.nonce[0] == 0) {
            nonce = RLP.encodeBytes(null);
        } else {
            nonce = RLP.encodeBytes(this.nonce);
        }
        byte[] gasPrice = RLP.encodeBytes(this.gasPrice);
        byte[] gasLimit = RLP.encodeBytes(this.gasLimit);
        byte[] receiveAddress = RLP.encodeBytes(this.receiveAddress);
        byte[] value = RLP.encodeBytes(this.value);
        byte[] data = RLP.encodeBytes(this.data);

        // Since EIP-155 use chainId for v
//        if (chainId == null) {
            rlpRaw = RLP.encodeList(nonce, gasPrice, gasLimit, receiveAddress,
                    value, data);
//        } else {
//            byte[] v, r, s;
//            v = RLP.encodeInt(chainId);
//            r = RLP.encodeElement(EMPTY_BYTE_ARRAY);
//            s = RLP.encodeElement(EMPTY_BYTE_ARRAY);
//            rlpRaw = RLP.encodeList(nonce, gasPrice, gasLimit, receiveAddress,
//                    value, data, v, r, s);
//        }
        return rlpRaw;
    }

    public byte[] getEncoded() {

        if (rlpEncoded != null) return rlpEncoded;

        // parse null as 0 for nonce
        byte[] nonce = null;
        if (this.nonce == null || this.nonce.length == 1 && this.nonce[0] == 0) {
            nonce = RLP.encodeBytes(null);
        } else {
            nonce = RLP.encodeBytes(this.nonce);
        }
        byte[] gasPrice = RLP.encodeBytes(this.gasPrice);
        byte[] gasLimit = RLP.encodeBytes(this.gasLimit);
        byte[] receiveAddress = RLP.encodeBytes(this.receiveAddress);
        byte[] value = RLP.encodeBytes(this.value);
        byte[] data = RLP.encodeBytes(this.data);

        byte[] v, r, s;

        if (signature != null) {
            r = RLP.encodeBytes(BigIntegers.asUnsignedByteArray(signature.r));
            s = RLP.encodeBytes(BigIntegers.asUnsignedByteArray(signature.s));
        } else {
            r = RLP.encodeBytes(EMPTY_BYTE_ARRAY);
            s = RLP.encodeBytes(EMPTY_BYTE_ARRAY);
        }

        this.rlpEncoded = RLP.encodeList(nonce, gasPrice, gasLimit,
                receiveAddress, value, data, r, s);

        this.hash = this.getHash();

        return rlpEncoded;
    }

    public String toString() {
        return toString(Integer.MAX_VALUE);
    }

    public String toString(int maxDataSize) {
        rlpParse();
        String dataS;
        if (this.data == null) {
            dataS = "";
        } else if (this.data.length < maxDataSize) {
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
        return this.gasLimit == null ? ByteUtil.ZERO_BYTE_ARRAY : this.gasLimit;
    }

    public byte[] getGasPrice() {
        this.rlpParse();
        return this.gasPrice == null ? ByteUtil.ZERO_BYTE_ARRAY : this.gasPrice;
    }

    public byte[] getReceiveAddress() {
        this.rlpParse();
        return this.receiveAddress;
    }


    public byte[] getValue() {
        this.rlpParse();
        return this.value == null ? ByteUtil.ZERO_BYTE_ARRAY : this.value;
    }

    public boolean isParsed() {
        return this.parsed;
    }


    public byte[] getNonce() {
        this.rlpParse();
        return this.nonce == null ? ByteUtil.ZERO_BYTE_ARRAY : this.nonce;
    }


}
