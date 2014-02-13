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
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import be.belgium.eid.eidcommon.FormattedTLV;
import be.belgium.eid.exceptions.TagNotFoundException;

/**
 * Test the {@link be.belgium.eid.eidcommon.FormattedTLV} class. We won't test
 * the constructor and the methods of the base class since they are already
 * tested in the base TLV class.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 28 Nov 2007
 */
public class FormattedTLVTest {

	/** Contains the class under test */
	private transient FormattedTLV fTlv;

	/** Data needed for the tests */
	private static final byte[] fTLVstream = { 0, 4, 'T', 'e', 's', 't', 1, 2,
			'U', 'A', 2, 0, 3, 2, '1', '0', 4, 4, 'a', 'b', 'c', 'd', 5, 1,
			'a', 6, 6, 'a', 'b', 'c', 'd', 'e', 'f', 7, 10, '1', '7', '.', '1',
			'0', '.', '2', '0', '0', '7', 8, 12, '1', '7', ' ', 'O', 'C', 'T',
			' ', ' ', '2', '0', '0', '7', 9, 12, '1', '7', ' ', 'O', 'K', 'T',
			' ', ' ', '2', '0', '0', '7', 10, 11, '7', ' ', 'M', 'A', 'R', 'Z',
			' ', '2', '0', '0', '7', 11, 1, '5', 12, 4, '1', '2', '3', '4', 13,
			5, '1', '2', '3', '4', '5' };
	private static final byte[] fBytesAt0 = { 'T', 'e', 's', 't' };
	private static final byte[] fBytesAt1 = { 'U', 'A' };

	@Before
	public void setUp() throws Exception {
		fTlv = new FormattedTLV(fTLVstream);
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#asciiData(byte)}.
	 */
	@Test
	public void testAsciiData() {
		final byte[] asciiDataAt0 = fTlv.asciiData((byte) 0);
		for (int i = 0; i < asciiDataAt0.length; i++) {
			assertEquals(fBytesAt0[i], asciiDataAt0[i]);
		}

		assertEquals(0, fTlv.tagData((byte) 2).length);
		assertNull(fTlv.asciiData((byte) 20));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#binaryData(byte)}.
	 */
	@Test
	public void testBinaryData() {
		final byte[] binaryDataAt0 = fTlv.binaryData((byte) 1);
		for (int i = 0; i < binaryDataAt0.length; i++) {
			assertEquals(fBytesAt1[i], binaryDataAt0[i]);
		}

		assertEquals(0, fTlv.tagData((byte) 2).length);
		assertNull(fTlv.asciiData((byte) 20));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#stringData(byte)}.
	 */
	@Test
	public void testStringData() {
		assertEquals("Test", fTlv.stringData((byte) 0));
		assertEquals("UA", fTlv.stringData((byte) 1));
		assertEquals("", fTlv.stringData((byte) 2));

		assertNull(fTlv.stringData((byte) 20));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#hexadecimalData(byte)}.
	 */
	@Test
	public void testHexadecimalData() {
		assertEquals("54657374", fTlv.hexadecimalData((byte) 0));
		assertEquals("5541", fTlv.hexadecimalData((byte) 1));
		assertEquals("", fTlv.hexadecimalData((byte) 2));

		assertNull(fTlv.hexadecimalData((byte) 20));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#dateData(byte, java.lang.String)}.
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testDateData() throws ParseException {
		final long date = 1192572000000L;

		// Test the retrieving of the date with no given format
		assertEquals(date, fTlv.dateData((byte) 7, "dd.MM.yyyy").getTime());

		// Test the French name for october in which nothing has to be changed
		assertEquals(date, fTlv.dateData((byte) 8, "dd MMM  yyyy").getTime());

		// Test the Dutch name for october in which the OKT needs to be replaced
		// by OCT
		assertEquals(date, fTlv.dateData((byte) 9, "dd MMM  yyyy").getTime());

		// Test the German name for march with non-ascii byteacters in it and
		// with as day 7 instead of 07
		assertEquals(1173222000000L, fTlv.dateData((byte) 10, "dd MMM yyyy")
				.getTime());
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#dateData(byte, java.lang.String)}.
	 * 
	 * @throws ParseException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDateDataPattern() throws ParseException {
		// Test the retrieving of the date with an invalid date format
		fTlv.dateData((byte) 7, "dd.NN.yyyy");
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#dateData(byte, java.lang.String)}.
	 * 
	 * @throws ParseException
	 */
	@Test(expected = NullPointerException.class)
	public void testDateDataNull() throws ParseException {
		// Test the retrieving of the date with no given format
		fTlv.dateData((byte) 6, null);
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#dateData(byte, java.lang.String)}.
	 * 
	 * @throws ParseException
	 */
	@Test(expected = ParseException.class)
	public void testDateDataParse() throws ParseException {
		// Test the retrieving of the date that doesn't match the given format
		fTlv.dateData((byte) 6, "yyyy.MM.dd");
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#integerData(byte)}.
	 * 
	 * @throws TagNotFoundException
	 */
	@Test
	public void testIntegerData() throws TagNotFoundException {
		// Test the fetching of integer data when everything is normal
		assertEquals(10, fTlv.integerData((byte) 3));

		// Test the fetching of integer data when not enough bytes are supplied
		assertEquals(0, fTlv.integerData((byte) 2));
		assertEquals(5, fTlv.integerData((byte) 11));

		// Test the fetching of integer data when too many bytes are supplied
		assertEquals(45, fTlv.integerData((byte) 13));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#integerData(byte)}.
	 * 
	 * @throws TagNotFoundException
	 */
	@Test(expected = TagNotFoundException.class)
	public void testIntegerDataNull() throws TagNotFoundException {
		fTlv.integerData((byte) 20);
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#longData(byte)}.
	 * 
	 * @throws TagNotFoundException
	 */
	@Test
	public void testLongData() throws TagNotFoundException {
		// Test the fetching of long data when everything is normal
		assertEquals(1234L, fTlv.longData((byte) 12));

		// Test the fetching of long data when not enough bytes are supplied
		assertEquals(0L, fTlv.longData((byte) 2));
		assertEquals(5L, fTlv.longData((byte) 11));

		// Test the fetching of long data when too many bytes are supplied
		assertEquals(2345L, fTlv.longData((byte) 13));
	}

	/**
	 * Test method for
	 * {@link be.belgium.eid.eidcommon.FormattedTLV#longData(byte)}.
	 * 
	 * @throws TagNotFoundException
	 */
	@Test(expected = TagNotFoundException.class)
	public void testLongDataNull() throws TagNotFoundException {
		fTlv.longData((byte) 20);
	}
}
