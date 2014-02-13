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
package be.belgium.eid.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import be.belgium.eid.eidcommon.ByteConverter;

/**
 * Test the {@link be.belgium.eid.eidcommon.ByteConverter} class.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
public class ByteConverterTest {

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.ByteConverter#hexify(char[])}.
	 */
	@Test
	public void testHexify() {
		// Tests the case where everything is OK
		final char[] data = { 'F', 'e', 'D', 'i', 'C', 't', };
		final char[] data2 = {};
		final char[] dataWithLetters = { 'e', 'I', 'D', 'L', 'i', 'b', 'r',
				'a', 'r', 'y' };
		assertEquals("466544694374", ByteConverter.hexify(data));

		// Tests the case where the given data is empty
		assertEquals("", ByteConverter.hexify(data2));

		// Tests the case where there are letters in the hexified string
		assertEquals(ByteConverter.hexify(dataWithLetters),
				"6549444C696272617279");

		// Test the unhexify in the fomer three cases
		assertEquals(new String(ByteConverter.unhexify(ByteConverter
				.hexify(data))), "FeDiCt");
		assertEquals(
				ByteConverter.unhexify(ByteConverter.hexify(data2)).length, 0);
		assertEquals(new String(ByteConverter.unhexify(ByteConverter
				.hexify(dataWithLetters))), "eIDLibrary");
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.ByteConverter#hexify(char[])}.
	 */
	@Test(expected = NullPointerException.class)
	public void testHexifyNull() {
		final byte[] bytes = null;
		ByteConverter.hexify(bytes);
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.ByteConverter#integerValue(byte, byte, boolean)}
	 * .
	 */
	@Test
	public void testIntegerValue() {
		// Test the conversion of bytes to integer for a very small value
		assertEquals(0, ByteConverter
				.integerValue((byte) '0', (byte) '0', true));

		// Test the conversion of bytes to integer for a normal value
		assertEquals(10, ByteConverter.integerValue((byte) '1', (byte) '0',
				true));
		assertEquals(1, ByteConverter.integerValue((byte) '1', (byte) '0',
				false));

		// Test the conversion of the maximum possible value
		assertEquals(99, ByteConverter.integerValue((byte) '9', (byte) '9',
				true));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.ByteConverter#longValue(byte, byte, byte, byte, boolean)}
	 * .
	 */
	@Test
	public void testLongValue() {
		// Test the conversion of bytes to long for a very small value
		assertEquals(0L, ByteConverter.longValue((byte) '0', (byte) '0',
				(byte) '0', (byte) '0', true));

		// Test the conversion of bytes to long for a normal value
		assertEquals(1234L, ByteConverter.longValue((byte) '1', (byte) '2',
				(byte) '3', (byte) '4', true));
		assertEquals(1234L, ByteConverter.longValue((byte) '4', (byte) '3',
				(byte) '2', (byte) '1', false));

		// Test the conversion of the maximum possible value
		assertEquals(9999L, ByteConverter.longValue((byte) '9', (byte) '9',
				(byte) '9', (byte) '9', true));
	}
}
