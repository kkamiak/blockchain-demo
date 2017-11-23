package by.instinctools.utils;


import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.util.BigIntegers.*;

/**
 * Created by haria on 23.11.17.
 */
public class RLP extends RlpEncoder{

    private static final Logger logger = LoggerFactory.getLogger("rlp");

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final byte[] ZERO_BYTE_ARRAY = new byte[]{0};

    public static final byte[] EMPTY_ELEMENT_RLP = encode(RlpString.create(new byte[0]));
    private static final double MAX_ITEM_LENGTH = Math.pow(256, 8);
    private static final int SIZE_THRESHOLD = 56;
    private static final int OFFSET_SHORT_ITEM = 0x80;
    private static final int OFFSET_LONG_ITEM = 0xb7;
    private static final int OFFSET_SHORT_LIST = 0xc0;
    private static final int OFFSET_LONG_LIST = 0xf7;

    public static RlpList decode(byte[] msgData) {
        RlpList rlpList = new RlpList(new ArrayList<>());
        fullTraverse(msgData, 0, 0, msgData.length, 1, rlpList);
        return rlpList;
    }

    private static void fullTraverse(byte[] msgData, int level, int startPos, int endPos, int levelToIndex, RlpList rlpList) {
        try {
            if (msgData != null && msgData.length != 0) {
                int e = startPos;

                while (true) {
                    while (e < endPos) {
                        logger.debug("fullTraverse: level: " + level + " startPos: " + e + " endPos: " + endPos);
                        byte[] rlpPrefix;
                        byte var14;
                        int var16;
                        if ((msgData[e] & 255) > 247) {
                            var14 = (byte) (msgData[e] - 247);
                            var16 = calcLength(var14, msgData, e);
                            if (var16 < 56) {
                                throw new RuntimeException("Short list has been encoded as long list");
                            }

                            if (var16 > msgData.length - e - var14) {
                                throw new RuntimeException("Parsed data lays outside of RLP length boundaries");
                            }

                            rlpPrefix = new byte[var14 + var16 + 1];
                            System.arraycopy(msgData, e, rlpPrefix, 0, var14 + var16 + 1);

                            List<RlpType> prefixes = new ArrayList<>();
                            prefixes.add(RlpString.create(rlpPrefix));

                            RlpList var19 = new RlpList(prefixes);

                            fullTraverse(msgData, level + 1, e + var14 + 1, e + var14 + var16 + 1, levelToIndex, var19);
                            rlpList.getValues().add(var19);
                            e += var14 + var16 + 1;
                        } else {
                            byte[] var15;
                            if ((msgData[e] & 255) >= 192 && (msgData[e] & 255) <= 247) {
                                var14 = (byte) ((msgData[e] & 255) - 192);
                                var15 = new byte[var14 + 1];
                                System.arraycopy(msgData, e, var15, 0, var14 + 1);

                                List<RlpType> var15List = new ArrayList<>();
                                var15List.add(RlpString.create(var15));

                                RlpList var17 = new RlpList(var15List);
                                if (var14 > 0) {
                                    fullTraverse(msgData, level + 1, e + 1, e + var14 + 1, levelToIndex, var17);
                                }

                                rlpList.getValues().add(var17);
                                e += 1 + var14;
                            } else if ((msgData[e] & 255) > 183 && (msgData[e] & 255) < 192) {
                                var14 = (byte) (msgData[e] - 183);
                                var16 = calcLength(var14, msgData, e);
                                if (var16 < 56) {
                                    throw new RuntimeException("Short item has been encoded as long item");
                                }

                                if (var16 > msgData.length - e - var14) {
                                    throw new RuntimeException("Parsed data lays outside of RLP length boundaries");
                                }

                                rlpPrefix = new byte[var16];
                                System.arraycopy(msgData, e + var14 + 1, rlpPrefix, 0, var16);
                                byte[] var18 = new byte[var14 + 1];
                                System.arraycopy(msgData, e, var18, 0, var14 + 1);

                                rlpList.getValues().add(RlpString.create(rlpPrefix));
                                e += var14 + var16 + 1;
                            } else if ((msgData[e] & 255) > 128 && (msgData[e] & 255) <= 183) {
                                var14 = (byte) ((msgData[e] & 255) - 128);
                                var15 = new byte[var14];
                                System.arraycopy(msgData, e + 1, var15, 0, var14);
                                if (var14 == 1 && (var15[0] & 255) < 128) {
                                    throw new RuntimeException("Single byte has been encoded as byte string");
                                }

                                rlpPrefix = new byte[2];
                                System.arraycopy(msgData, e, rlpPrefix, 0, 2);

                                rlpList.getValues().add(RlpString.create(var15));
                                e += 1 + var14;
                            } else {
                                byte[] item;
                                RlpString rlpItem;
                                if ((msgData[e] & 255) == 128) {
                                    item = EMPTY_BYTE_ARRAY;
                                    rlpList.getValues().add(RlpString.create(item));
                                    ++e;
                                } else if ((msgData[e] & 255) < 128) {
                                    item = new byte[]{(byte) (msgData[e] & 255)};
                                    rlpList.getValues().add(RlpString.create(item));
                                    ++e;
                                }
                            }
                        }
                    }

                    return;
                }
            }
        } catch (Exception var12) {
            throw new RuntimeException("RLP wrong encoding (" + Hex.toHexString(msgData, startPos, endPos - startPos) + ")", var12);
        } catch (OutOfMemoryError var13) {
            throw new RuntimeException("Invalid RLP (excessive mem allocation while parsing) (" + Hex.toHexString(msgData, startPos, endPos - startPos) + ")", var13);
        }
    }

    public static byte[] encodeBytes(byte [] value) {
       return encode(RlpString.create(value));
    }

    private static int calcLength(int lengthOfLength, byte[] msgData, int pos) {
        byte pow = (byte) (lengthOfLength - 1);
        int length = 0;

        for (int i = 1; i <= lengthOfLength; ++i) {
            int bt = msgData[pos + i] & 255;
            int shift = 8 * pow;
            if (bt == 0 && length == 0) {
                throw new RuntimeException("RLP length contains leading zeros");
            }

            if (32 - Integer.numberOfLeadingZeros(bt) + shift > 31) {
                return 2147483647;
            }

            length += bt << shift;
            --pow;
        }

        return length;
    }

    public static byte[] encodeList(byte[]... elements) {

        if (elements == null) {
            return new byte[]{(byte) OFFSET_SHORT_LIST};
        }

        int totalLength = 0;
        for (byte[] element1 : elements) {
            totalLength += element1.length;
        }

        byte[] data;
        int copyPos;
        if (totalLength < SIZE_THRESHOLD) {

            data = new byte[1 + totalLength];
            data[0] = (byte) (OFFSET_SHORT_LIST + totalLength);
            copyPos = 1;
        } else {
            // length of length = BX
            // prefix = [BX, [length]]
            int tmpLength = totalLength;
            byte byteNum = 0;
            while (tmpLength != 0) {
                ++byteNum;
                tmpLength = tmpLength >> 8;
            }
            tmpLength = totalLength;
            byte[] lenBytes = new byte[byteNum];
            for (int i = 0; i < byteNum; ++i) {
                lenBytes[byteNum - 1 - i] = (byte) ((tmpLength >> (8 * i)) & 0xFF);
            }
            // first byte = F7 + bytes.length
            data = new byte[1 + lenBytes.length + totalLength];
            data[0] = (byte) (OFFSET_LONG_LIST + byteNum);
            System.arraycopy(lenBytes, 0, data, 1, lenBytes.length);

            copyPos = lenBytes.length + 1;
        }
        for (byte[] element : elements) {
            System.arraycopy(element, 0, data, copyPos, element.length);
            copyPos += element.length;
        }
        return data;
    }

    public static byte[] encodeBigInteger(BigInteger srcBigInteger) {
        if (srcBigInteger.equals(BigInteger.ZERO))
            return encodeByte((byte) 0);
        else {
            return encodeBytes(asUnsignedByteArray(srcBigInteger));
        }
    }

    public static byte[] encodeByte(byte singleByte) {
        if ((singleByte & 0xFF) == 0) {
            return new byte[]{(byte) OFFSET_SHORT_ITEM};
        } else if ((singleByte & 0xFF) <= 0x7F) {
            return new byte[]{singleByte};
        } else {
            return new byte[]{(byte) (OFFSET_SHORT_ITEM + 1), singleByte};
        }
    }

}
