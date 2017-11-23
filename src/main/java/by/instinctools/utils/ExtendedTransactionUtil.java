package by.instinctools.utils;

import org.web3j.crypto.TransactionUtils;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * Created by haria on 23.11.17.
 */
public class ExtendedTransactionUtil extends TransactionUtils {

    private static final int STRING_OFFSET = 0x80;
    private static final int LIST_OFFSET = 0xc0;


    public static Transaction decodeTransaction (byte []  bytes) {

        byte [] hash;
        byte [] nonce;
        byte [] gasPrice;
        byte [] gasLimit;
        byte [] receiveAddress;
        byte [] value;
        byte [] data;
        Integer chainId;

        try {

            RlpList e = RlpDecoder.decode(bytes);

            RlpList transaction = (RlpList) e.getValues().get(0);
            if(transaction.getValues().size() > 9) {
                throw new RuntimeException("Too many RLP elements");
            } else {
                Iterator vData = transaction.getValues().iterator();

                RlpType v;
                do {
                    if(!vData.hasNext()) {
                        nonce = ((RlpString)transaction.getValues().get(0)).getBytes();
                        gasPrice = ((RlpString)transaction.getValues().get(1)).getBytes();
                        gasLimit = ((RlpString)transaction.getValues().get(2)).getBytes();
                        receiveAddress = ((RlpString)transaction.getValues().get(3)).getBytes();
                        value = ((RlpString)transaction.getValues().get(4)).getBytes();
                        data = ((RlpString)transaction.getValues().get(5)).getBytes();
                        if(((RlpString)transaction.getValues().get(6)).getBytes() != null) {
                            byte[] vData1 = ((RlpString)transaction.getValues().get(6)).getBytes();
                            BigInteger v1 = new BigInteger(vData1);
                            chainId = extractChainIdFromV(v1);
                            byte[] r = ((RlpString)transaction.getValues().get(7)).getBytes();
                            byte[] s = ((RlpString)transaction.getValues().get(8)).getBytes();
//                            this.signature = ECDSASignature.fromComponents(r, s, this.getRealV(v1));
                        } else {
//                            logger.debug("RLP encoded tx is not signed!");
                        }


                        return new Transaction("", new BigInteger(nonce), new BigInteger(gasPrice),
                                new BigInteger(gasLimit), new BigInteger(receiveAddress).toString(), new BigInteger(value),
                                new BigInteger(receiveAddress).toString());


                    }

                    v = (RlpString)vData.next();
                } while(v instanceof RlpString);

                throw new RuntimeException("Transaction RLP elements shouldn\'t be lists");
            }
        } catch (Exception var7) {
            throw new RuntimeException("Error on parsing RLP", var7);
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


}
