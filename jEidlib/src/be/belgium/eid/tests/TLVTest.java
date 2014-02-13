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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import org.junit.Test;

import be.belgium.eid.eidcommon.TLV;

/**
 * Test the {@link be.belgium.eid.eidcommon.TLV} class.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 27 Nov 2007
 */
public class TLVTest {

	/** Data needed for the tests */
	private final byte[] fTLVstream = { 1, 4, 'T', 'e', 's', 't', 2, 3, 'f',
			'o', 'r', 3, 3, 'T', 'L', 'V', 10, 0 };
	private final byte[] fTLVstream2 = { 0, 6, 'F', 'e', 'd', 'I', 'C', 'T' };
	private final byte[] fBytesAt0 = { 'F', 'e', 'd', 'I', 'C', 'T' };
	private final byte[] fBytesAt1 = { 'T', 'e', 's', 't' };
	private final byte[] fBytesAt2 = { 'f', 'o', 'r' };
	private final byte[] fBytesAt3 = { 'T', 'L', 'V' };

	/**
	 * Test method for {@link be.belgium.eid.eidcommon.TLV#TLV()}.
	 */
	@Test
	public void testTLV() {
		// After creating a TLV with no given data, the elements should be empty
		final TLV tlv = new TLV();
		final AbstractMap<Byte, byte[]> elements = tlv.getTLVElements();
		assertTrue(elements.keySet().isEmpty());
		assertTrue(elements.values().isEmpty());
		assertNull(tlv.tagData((byte) 0));
	}

	/**
	 * Test method for {@link be.belgium.eid.eidcommon.TLV#TLV(byte[])}.
	 */
	@Test
	public void testTLVChar() {
		// We create an ordinary valid TLV
		final TLV tlv = new TLV(fTLVstream);
		final AbstractMap<Byte, byte[]> elements = tlv.getTLVElements();

		// There are three keys in our example TLV: 1, 2 and 3
		assertFalse(elements.keySet().contains((byte) 0));
		assertTrue(elements.keySet().contains((byte) 1));
		assertTrue(elements.keySet().contains((byte) 2));
		assertTrue(elements.keySet().contains((byte) 3));
		assertFalse(elements.keySet().contains((byte) 4));

		// Fetch the data corresponding with the keys to check whether they are
		// correctly mapped
		final byte[] fetchedBytesAt1 = elements.get((byte) 1);
		final byte[] tagDataAt1 = tlv.tagData((byte) 1);
		for (int i = 0; i < fetchedBytesAt1.length; i++) {
			assertEquals(fBytesAt1[i], fetchedBytesAt1[i]);
			assertEquals(fBytesAt1[i], tagDataAt1[i]);
		}

		final byte[] fetchedBytesAt2 = elements.get((byte) 2);
		final byte[] tagDataAt2 = tlv.tagData((byte) 2);
		for (int i = 0; i < fetchedBytesAt2.length; i++) {
			assertEquals(fBytesAt2[i], fetchedBytesAt2[i]);
			assertEquals(fBytesAt2[i], tagDataAt2[i]);
		}

		final byte[] fetchedBytesAt3 = elements.get((byte) 3);
		final byte[] tagDataAt3 = tlv.tagData((byte) 3);
		for (int i = 0; i < fetchedBytesAt3.length; i++) {
			assertEquals(fBytesAt3[i], fetchedBytesAt3[i]);
			assertEquals(fBytesAt3[i], tagDataAt3[i]);
		}

		assertEquals(0, tlv.tagData((byte) 10).length);
		assertNull(tlv.tagData((byte) 0));
	}

	/**
	 * Test method for {@link be.belgium.eid.eidcommon.TLV#TLV(byte[])}.
	 */
	@Test(expected = NullPointerException.class)
	public void testTLVCharNull() {
		new TLV(null);
	}

	/**
	 * Test method for {@link be.belgium.eid.eidcommon.TLV#parse(byte[])}.
	 */
	@Test
	public void testParse() {
		final TLV tlv = new TLV();

		// There are currently no tags in our TLV
		assertNull(tlv.tagData((byte) -1));
		assertNull(tlv.tagData((byte) 0));
		assertNull(tlv.tagData((byte) 1));
		assertNull(tlv.tagData((byte) 2));
		assertNull(tlv.tagData((byte) 3));

		// Parse the first stream of data
		tlv.parse(fTLVstream);

		// Check the results
		assertNull(tlv.tagData((byte) -1));
		assertNull(tlv.tagData((byte) 0));

		final byte[] tagDataAt1 = tlv.tagData((byte) 1);
		for (int i = 0; i < tagDataAt1.length; i++) {
			assertEquals(fBytesAt1[i], tagDataAt1[i]);
		}

		final byte[] tagDataAt2 = tlv.tagData((byte) 2);
		for (int i = 0; i < tagDataAt2.length; i++) {
			assertEquals(fBytesAt2[i], tagDataAt2[i]);
		}

		final byte[] tagDataAt3 = tlv.tagData((byte) 3);
		for (int i = 0; i < tagDataAt3.length; i++) {
			assertEquals(fBytesAt3[i], tagDataAt3[i]);
		}

		assertEquals(0, tlv.tagData((byte) 10).length);

		// Now parse the second stream of data in which the tag 0 is also filled
		tlv.parse(fTLVstream2);

		assertNull(tlv.tagData((byte) -1));

		final byte[] tagDataAt0 = tlv.tagData((byte) 0);
		for (int i = 0; i < tagDataAt0.length; i++) {
			assertEquals(fBytesAt0[i], tagDataAt0[i]);
		}
	}

	/**
	 * Test method for {@link be.belgium.eid.eidcommon.TLV#parse(byte[])}.
	 */
	@Test(expected = NullPointerException.class)
	public void testParseNull() {
		(new TLV()).parse(null);
	}
}
