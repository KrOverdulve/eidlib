/*
 * 5. LICENSE ISSUES 
 * The eID Toolkit uses several third-party libraries or code. 
 * Redistributions in any form of the eID Toolkit � even embedded in a compiled application � 
 * must reproduce all the eID Toolkit and third-party�s copyright notices, list of conditions, 
 * disclaimers, and any other materials provided with the distribution. 
 * 
 * 5.1 Disclaimer 
 * This eID Toolkit is provided by the Belgian Government �as is�, and any expressed or implied 
 * warranties, including, but not limited to, the implied warranties of merchantability and fitness 
 * for a particular purpose are disclaimed.  In no event shall the Belgian Government or its 
 * contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential 
 * damages (including, but not limited to, procurement of substitute goods or services; loss of use, 
 * data, or profits; or business interruption) however caused and on any theory of liability, whether 
 * in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of 
 * the use of this Toolkit, even if advised of the possibility of such damage. 
 * However, the Belgian Government will ensure the maintenance of the Toolkit � that is, bug 
 * fixing, and support of new versions of the Electronic Identity card.
 * 
 * Source: DeveloperGuide.pdf
 */
package be.belgium.eid.eidcommon;

/**
 * The byte converter class is primarily used as a helper class to convert
 * sequences of bytes to other standard data types and printable formats.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
public class ByteConverter {

	// Contains the hexadecimal characters in a printable format
	public static char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Represents sequences of characters in a printable format. When we would
	 * regularly print out a byte there is a good chance we couldn't see the
	 * actual value of the byte due to non-printable characters in the ASCII
	 * table. Therefore we prefer to represent a string of bytes as a string of
	 * nibbles. Each nibble consists of 4 bits and therefore represents a number
	 * in the hexadecimal system that we will return in our result. <br />
	 * <b>E.g.:</b> "Hello World!" is converted to "48656C6C6F20576F726C6421".
	 * 
	 * @param data
	 *            is the string of characters to convert to the printable format
	 * @return the data in a series of nibbles represented as hexadecimal
	 *         numbers
	 */
	public static String hexify(final char[] data) {
		final StringBuffer buf = new StringBuffer();

		// Traverse all the characters
		for (char c : data) {
			buf.append(hexify(c));
		}

		return buf.toString();
	}

	/**
	 * Represents sequences of bytes in a printable format. When we would
	 * regularly print out a byte there is a good chance we couldn't see the
	 * actual value of the byte due to non-printable characters in the ASCII
	 * table. Therefore we prefer to represent a string of bytes as a string of
	 * nibbles. Each nibble consists of 4 bits and therefore represents a number
	 * in the hexadecimal system that we will return in our result.
	 * 
	 * @param data
	 *            is the string of bytes to convert to the printable format
	 * @return the data in a series of nibbles represented as hexadecimal
	 *         numbers
	 */
	public static String hexify(final byte[] data) {
		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('A' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Represents bytes in a printable format. When we would regularly print out
	 * a byte there is a good chance we couldn't see the actual value of the
	 * byte due to non-printable characters in the ASCII table. Therefore we
	 * prefer to represent a string of bytes as a string of nibbles. Each nibble
	 * consists of 4 bits and therefore represents a number in the hexadecimal
	 * system that we will return in our result. <br /> <b>E.g.:</b> "H" is
	 * converted to "48".
	 * 
	 * @param data
	 *            is the byte to convert to the printable format
	 * @return the data in a series of nibbles represented as hexadecimal
	 *         numbers
	 */
	public static String hexify(final char data) {
		// We split the byte in two nibbles since a nibble represents a
		// hexadecimal character, note that 16 is 2^4
		final int firstNibble = (data / 16) % 16;
		final int secondNibble = (data - (firstNibble * 16)) % 16;

		// We add the appropriate hexadecimal character to the result
		return new Character(hexChars[firstNibble]).toString()
				+ new Character(hexChars[secondNibble]).toString();
	}

	/**
	 * Represents bytes in a printable format. When we would regularly print out
	 * a byte there is a good chance we couldn't see the actual value of the
	 * byte due to non-printable characters in the ASCII table. Therefore we
	 * prefer to represent a string of bytes as a string of nibbles. Each nibble
	 * consists of 4 bits and therefore represents a number in the hexadecimal
	 * system that we will return in our result. <br /> <b>E.g.:</b> "H" is
	 * converted to "48".
	 * 
	 * @param data
	 *            is the byte to convert to the printable format
	 * @return the data in a series of nibbles represented as hexadecimal
	 *         numbers
	 */
	public static String hexify(final byte data) {
		byte[] dataArr = new byte[] { data };
		return ByteConverter.hexify(dataArr);
	}

	/**
	 * Represents hexified bytes as documented above in their original value.
	 * 
	 * @param data
	 *            the hexified bytes in string format
	 */
	public static byte[] unhexify(final String data) {
		String evenData = data;

		// Make data multiple of two
		if ((evenData.length() / 2) * 2 != evenData.length()) {
			evenData += "0";
		}
		byte[] result = new byte[data.length() / 2];

		for (int i = 0; i < data.length(); i += 2) {
			final int firstDig = hexToDec(data.charAt(i)) * 16;
			final int secondDig = hexToDec(data.charAt(i + 1));

			result[i / 2] = (byte) (firstDig + secondDig);
		}

		return result;
	}

	/**
	 * Parses the current digit from hexadecimal to the equivalent decimal form.
	 * 
	 * @param digit
	 *            is the hexadecimal digit/letter
	 * @return the decimal equivalent form
	 */
	private static int hexToDec(final char digit) {
		int result = 0;

		if ((digit >= '0') && (digit <= '9')) {
			result = (int) digit - (int) '0';
		} else {
			result = (int) digit - (int) 'A' + 10;
		}

		assert ((result >= 0) && (result <= 15));

		return result;
	}

	/**
	 * Returns the integer value as indicated by the given two bytes.
	 * 
	 * @param firstByte
	 *            is the first byte to convert
	 * @param secondByte
	 *            is the second byte to convert
	 * @param msbFirst
	 *            indicates whether the value has to be calculated as most
	 *            significant byte first or not (in which case it is least
	 *            significant byte first)
	 * @return the integer value
	 */
	public static int integerValue(final byte firstByte, final byte secondByte,
			final boolean msbFirst) {
		// Now we have with the ASCII representation of the bytes, what we
		// really want are the actual integer values that they represent
		final int firstInt = firstByte - '0';
		final int secondInt = secondByte - '0';

		// Most significant byte first
		if (msbFirst) {
			return (10 * firstInt + secondInt);
		} else {
			return (10 * secondInt + firstInt);
		}
	}

	/**
	 * Returns the long value as indicated by the given four bytes.
	 * 
	 * @param firstByte
	 *            is the first byte to convert
	 * @param secondByte
	 *            is the second byte to convert
	 * @param thirdByte
	 *            is the third byte to convert
	 * @param fourthByte
	 *            is the fourth byte to convert
	 * @param msbFirst
	 *            indicates whether the value has to be calculated as most
	 *            significant byte first or not (in which case it is least
	 *            significant byte first)
	 * @return the long value
	 */
	public static long longValue(final byte firstByte, final byte secondByte,
			final byte thirdByte, final byte fourthByte, final boolean msbFirst) {
		// Now we have with the ASCII representation of the bytes, what we
		// really want are the actual integer values that they represent
		final int firstInt = firstByte - '0';
		final int secondInt = secondByte - '0';
		final int thirdInt = thirdByte - '0';
		final int fourthInt = fourthByte - '0';

		// Most significant byte first
		if (msbFirst) {
			return (1000L * firstInt + 100L * secondInt + 10L * thirdInt + fourthInt);
		} else {
			return (1000L * fourthInt + 100L * thirdInt + 10L * secondInt + firstInt);
		}
	}
}
