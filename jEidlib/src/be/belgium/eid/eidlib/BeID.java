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
package be.belgium.eid.eidlib;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import be.belgium.eid.eidcommon.HTTPFileDownload;
import be.belgium.eid.event.CardAlivePromptTask;
import be.belgium.eid.event.CardListener;
import be.belgium.eid.exceptions.EIDException;
import be.belgium.eid.exceptions.HashVerificationException;
import be.belgium.eid.exceptions.InvalidSWException;
import be.belgium.eid.exceptions.RootVerificationException;
import be.belgium.eid.exceptions.SignatureVerificationException;
import be.belgium.eid.objects.IDAddress;
import be.belgium.eid.objects.IDData;
import be.belgium.eid.objects.IDPhoto;
import be.belgium.eid.objects.IDTokenInfo;
import be.belgium.eid.objects.IDVersion;
import be.belgium.eid.security.Certificate;
import be.belgium.eid.security.CertificateChain;
import be.belgium.eid.security.CertificateStatus;
import be.belgium.eid.security.HardCodedRootCertificate;
import be.belgium.eid.security.HardCodedRootCertificateV2;
import be.belgium.eid.security.OCSPClient;
import be.belgium.eid.security.RNCertificate;

/**
 * The beID class is the main interface to perform operations on the Belgian eID
 * card and to perform verification, sign documents, etc... . This interface
 * should be used when trying to perform high-level operations in your own
 * system. Every operation connects to the smart card if not yet connected
 * before performing the operation itself.
 * 
 * TODO Check SW's after every execution of transmitAPDU. Perhaps check the SW
 * there and throw the exception
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 07 Dec 2007
 */
public class BeID extends SmartCard {

	/** Contains the types of signatures */
	public enum SignatureType {
		AUTHENTICATIONSIG, NONREPUDIATIONSIG
	};

	/** Indicates whether test cards with an invalid root are enabled */
	private boolean fEnableTestCard;

	/**
	 * Contains the name of the smart card reader to connect with, leave empty
	 * to connect with the first available smart card reader
	 */
	private String fName;

	/** Contains the listener when the card is inserted or removed */
	private CardListener fCardListener = null;

	/**
	 * Contains the thread in which the card listener is prompting for the
	 * presence of a smart card.
	 */
	private CardAlivePromptTask fCardListenThread;

	/**
	 * Sets up the requirements needed for a valid functioning. The system
	 * connects to the smart card reader with the given name. The reader used is
	 * an open SC reader using the PC/SC protocol.
	 * 
	 * @param name
	 *            is the name of the smart card reader to connect with
	 * @param enableTestCard
	 *            indicates whether test cards with invalid roots are enabled
	 */
	public BeID(final String name, final boolean enableTestCard) {
		super();
		fEnableTestCard = enableTestCard;
		fName = name;
	}

	/**
	 * Sets up the requirements needed for a valid functioning. The system
	 * connects to the first smart card reader that is found in the system and
	 * that works with the PC/SC driver.
	 * 
	 * @param enableTestCard
	 *            indicates whether test cards with invalid roots are enabled
	 */
	public BeID(final boolean enableTestCard) {
		super();
		fEnableTestCard = enableTestCard;
		fName = "";
	}

	/**
	 * Connects the smart card to the system if possible. This operation should
	 * only be performed when calling a method of the class
	 * {@link be.belgium.eid.eidlib.SmartCard}. When calling a method of the
	 * BeID class itself, this method is implicitly executed. It does however
	 * not harm to perform this operation multiple times since nothing happens
	 * when already connected. Prefer to use this method to using the connect 
	 * methods of the SmartCard class.
	 * 
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public void connect() throws EIDException {
		try {
			if ("".equals(fName)) {
				super.connectCard();
			} else {
				super.connectCard(fName);
			}
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the ID information from the card currently inserted in the smart
	 * card reader. This data includes the personal information and some ID
	 * specific information.
	 * 
	 * @return the ID information
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public IDData getIDData() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			// Read data and signature of ID
			byte[] readData;
			byte[] readSignatureData;
			final byte[] fileToRead = { IDData.fgDFID[0], IDData.fgDFID[1],
					IDData.fgDataTag, IDData.fgDataTagID };
			final byte[] signatureFileToRead = { IDData.fgDFID[0],
					IDData.fgDFID[1], IDData.fgDataTag, IDData.fgDataTagIDSIG };

			readData = super.readFile(fileToRead, IDData.fgMAX_RAW_LEN);
			readSignatureData = super.readFile(signatureFileToRead,
					IDData.fgMAX_SIGNATURE_LEN);

			// Verify the root and the signature
			if (verifyRoot()) {
				if (verifyRNSignature(readData, readSignatureData)) {
					// Return read identity data
					return IDData.parse(readData);
				} else {
					throw new SignatureVerificationException("ID");
				}
				// } else {
				// throw new RootVerificationException();
			} else {
				return null;
			}
		} catch (EIDException e) {
			// We don't need another wrap around
			throw e;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the address of the holder of the eID card currently inserted in
	 * the smart card reader.
	 * 
	 * @return the address of the holder
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public IDAddress getIDAddress() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			// Read the address data, the ID signature and the address signature
			byte[] readDataRaw;
			byte[] readIDSignature;
			byte[] readAddrSignature;
			final byte[] fileToRead = { IDAddress.fgDFID[0],
					IDAddress.fgDFID[1], IDAddress.fgDataTag,
					IDAddress.fgDataTagADDR };
			final byte[] idSigFileToRead = { IDData.fgDFID[0],
					IDData.fgDFID[1], IDData.fgDataTag, IDData.fgDataTagIDSIG };
			final byte[] addrSigFileToRead = { IDAddress.fgDFID[0],
					IDAddress.fgDFID[1], IDAddress.fgDataTag,
					IDAddress.fgDataTagADDRSIG };

			readIDSignature = super.readFile(idSigFileToRead,
					IDAddress.fgMAX_SIGNATURE_LEN);
			readAddrSignature = super.readFile(addrSigFileToRead,
					IDAddress.fgMAX_SIGNATURE_LEN);
			readDataRaw = super.readFile(fileToRead, IDAddress.fgMAX_RAW_LEN);

			// Trim trailing zeroes of read data
			int indexLastNonZero = -1;
			for (int i = readDataRaw.length - 1; i >= 0; i--) {
				if (readDataRaw[i] != 0) {
					indexLastNonZero = i;
					break;
				}
			}
			final byte[] readData = new byte[indexLastNonZero + 1];
			System.arraycopy(readDataRaw, 0, readData, 0, indexLastNonZero + 1);

			// Append the ID signature
			byte[] fullData = new byte[readData.length + readIDSignature.length];
			System.arraycopy(readData, 0, fullData, 0, readData.length);
			System.arraycopy(readIDSignature, 0, fullData, readData.length,
					readIDSignature.length);

			// Verify the root and the signature
			if (verifyRoot()) {
				if (verifyRNSignature(fullData, readAddrSignature)) {
					// Return read address data
					return IDAddress.parse(readData);
				} else {
					throw new SignatureVerificationException("Address");
				}
			} else {
				throw new RootVerificationException();
			}
		} catch (EIDException e) {
			// We don't need another wrap around
			throw e;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the photo of the holder of the eID card currently inserted in the
	 * smart card reader.
	 * 
	 * @return the photo data
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public IDPhoto getIDPhoto() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			// Read file
			byte[] readData = new byte[] {};
			final byte[] fileToRead = { IDPhoto.fgDFID[0], IDPhoto.fgDFID[1],
					IDPhoto.fgDataTag, IDPhoto.fgDataTagPHOTO };

			readData = super.readFile(fileToRead, IDPhoto.fgMAX_RAW_LEN);

			// Parse data and verify whether correct root and hash
			final IDPhoto photo = IDPhoto.parse(readData);
			// Verify the root and the signature
			if (verifyRoot()) {
				if (photo.verifyHash(this.getIDData().getHashPhoto())) {
					return photo;
				} else {
					throw new HashVerificationException("Photo");
				}
			} else {
				throw new RootVerificationException();
			}
		} catch (EIDException e) {
			// We don't need another wrap around
			throw e;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the version information of the currently inserted smart card.
	 * 
	 * @return the version information
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public IDVersion getIDVersionInformation() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			// Read token information
			byte[] readData = new byte[] {};
			final byte[] fileToRead = { IDTokenInfo.fgDFCert[0],
					IDTokenInfo.fgDFCert[1], IDTokenInfo.fgTokenInfo[0],
					IDTokenInfo.fgTokenInfo[1] };

			readData = super.readFile(fileToRead, IDTokenInfo.fgOFFSET + 4);

			// Return data
			return IDVersion.parse(super.getCardData(), readData);
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the certificates in a certificate validation chain.
	 * 
	 * @return the certificate chain
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public CertificateChain getCertificateChain() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			return new CertificateChain(this);
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Returns the national register certificate.
	 * 
	 * @return the RN certificate
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public RNCertificate getNationalRegisterCertificate() throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			final RNCertificate rn = new RNCertificate(this);
			rn.verify();
			return rn;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Generates a signature for the given data. This signature is calculated on
	 * the SHA1 hash of the given data. The signature can later be verified by
	 * anyone to assure that the data sent was really yours.
	 * 
	 * @param data
	 *            is the data to generate a signature for. Although a byte array
	 *            is not the most easy format to work with, it is the most
	 *            versatile format. When the data to verify is just a string,
	 *            perform {@link java.lang.String#getBytes()} and pass the
	 *            resulting byte array as argument
	 * @param pinCode
	 *            is the PIN code of the card needed to provide secure signing
	 * @param sigType
	 *            is the type of signature to use, either an authentication
	 *            signature or a non repudiation signature
	 * @return the generated signature. This signature can later be saved to
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public byte[] generateSignature(byte[] data, String pinCode,
			SignatureType sigType) throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(data);

			// Prepare the smart card for signing
			byte[] preparationData;
			if (sigType.equals(SignatureType.AUTHENTICATIONSIG)) {
				preparationData = new byte[] { (byte) 0x04, (byte) 0x80,
						(byte) 0x02, (byte) 0x84, (byte) 0x82 };
			} else if (sigType.equals(SignatureType.NONREPUDIATIONSIG)) {
				preparationData = new byte[] { (byte) 0x04, (byte) 0x80,
						(byte) 0x02, (byte) 0x84, (byte) 0x83 };
			} else {
				// Well kind of impossible...
				preparationData = new byte[] {};
			}

			super.transmitAPDU(new CommandAPDU(0x00, 0x22, 0x41, 0xB6,
					preparationData));

			// Verify the PIN code first, otherwise the signing will return an
			// error
			this.verifyPIN(pinCode);

			// Generate signature
			ResponseAPDU rAPDU = super.transmitAPDU(new CommandAPDU(0x00, 0x2A,
					0x9E, 0x9A, md.digest()));

			// Check SW
			if ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00)) {
				return rAPDU.getData();
			} else {
				throw new InvalidSWException(rAPDU.getSW1(), rAPDU.getSW2());
			}
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Verifies the signature by verifying the buffer against the signature,
	 * using the given public key and the algorithm SHA1withRSA.
	 * 
	 * @param data
	 *            is the data to verify
	 * @param signature
	 *            is the signature to verify against the data
	 * @param sigType
	 *            is the type of signature to verify
	 * @return whether the verification succeeded or not
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public boolean verifySignature(final byte[] data, final byte[] signature,
			SignatureType sigType) throws EIDException {
		try {
			// Initialize signature with correct algorithm
			Signature sig = null;
			try {
				sig = Signature.getInstance("SHA1withRSA");
			} catch (NoSuchAlgorithmException e) {
				// Shouldn't occur
				throw e;
			}

			// Fetch public key of correct certificate
			PublicKey pk = null;
			if (sigType.equals(SignatureType.AUTHENTICATIONSIG)) {
				pk = this.getCertificateChain().getAuthenticationCert()
						.getX509Certificate().getPublicKey();
			} else if (sigType.equals(SignatureType.NONREPUDIATIONSIG)) {
				pk = this.getCertificateChain().getSignatureCert()
						.getX509Certificate().getPublicKey();
			} else {
				// Well kind of impossible...
			}

			// Verify signature and return results
			sig.initVerify(pk);
			sig.update(data, 0, data.length);
			return sig.verify(signature);
		} catch (EIDException e) {
			// We don't need another wrap around
			throw e;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Verifies the certificates on the currently inserted eID card by sending a
	 * OCSP request to a certified OCSP responder whose host address is located
	 * on the eID certificates. The returning value indicates whether everything
	 * went fine. If everything went fine the status of all certificates is set
	 * to OK. If there is a revocation of some kind, false is returned and the
	 * revoked certificates have a status that contains the reason for failure.
	 * 
	 * @param certChain
	 *            is the certificate chain to verify, the status of the
	 *            certificates in this chain are adjusted
	 * @return whether the verification succeeded or not
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public boolean verifyOCSP(final CertificateChain certChain)
			throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			boolean allOk = true; // Indicates whether all certificates were
			// validated well

			// Put all remaining certificates in list
			final List<Certificate> certifs = new ArrayList<Certificate>();
			certifs.add(certChain.getAuthenticationCert());
			certifs.add(certChain.getSignatureCert());

			// Verify every certificate if they're not verified yet
			for (Certificate certif : certifs) {
				final X509Certificate x509 = certif.getX509Certificate();

				// Fetch the authority access point where to verify the OCSP
				final String AUHORITY_INFO_ACCESS_OID = "1.3.6.1.5.5.7.1.1";
				if (x509.getNonCriticalExtensionOIDs().contains(
						AUHORITY_INFO_ACCESS_OID)) {
					final String extensionValue = new String(x509
							.getExtensionValue(AUHORITY_INFO_ACCESS_OID));
					final String url = extensionValue.substring(extensionValue
							.lastIndexOf("http://"));

					// Verify via OCSP
					if (!OCSPClient.processOCSPRequest(certChain
							.getCertificateAuthorityCert(), certif, url)) {
						allOk = false;
					}
				}
			}

			return allOk;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Verifies the certificates on the currently inserted eID card by using
	 * certificate revocation lists (CRLs). The returning value indicates
	 * whether everything went fine. If everything went fine the status of all
	 * certificates is set to OK. If there is a revocation of some kind, false
	 * is returned and the revoked certificates have a status that contains the
	 * reason for failure.
	 * 
	 * @param certChain
	 *            is the certificate chain to verify, the status of the
	 *            certificates in this chain are adjusted
	 * @param rnCert
	 *            is the National Register certificate to verify, the status of
	 *            this certificate is adjusted when performing this action.
	 * @return whether the verification succeeded or not
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	public boolean verifyCRL(final CertificateChain certChain,
			final RNCertificate rnCert) throws EIDException {
		try {
			// Connect if not yet connected
			this.connectCard();

			boolean allOk = true; // Indicates whether all certificates were
			// validated well

			// Root certificate is self signed and can't be verified against a
			// CRL
			final Certificate cert = certChain.getRootCert();

			if (cert.getStatus().equals(
					CertificateStatus.BEID_CERTSTATUS_CERT_NOT_VALIDATED)) {
				if (cert.getX509Certificate().getSubjectX500Principal()
						.getName().equals(
								cert.getX509Certificate()
										.getIssuerX500Principal().getName())) {
					cert
							.setStatus(CertificateStatus.BEID_CERTSTATUS_SELF_SIGNED_CERT_IN_CHAIN);
				} else {
					// Hmm, root certificate ought to be self signed
					cert
							.setStatus(CertificateStatus.BEID_CERTSTATUS_CERT_NOT_VALIDATED);
				}
			}

			// Put all remaining certificates in list
			final List<Certificate> certifs = new ArrayList<Certificate>();
			certifs.add(certChain.getCertificateAuthorityCert());
			certifs.add(certChain.getAuthenticationCert());
			certifs.add(certChain.getSignatureCert());
			certifs.add(rnCert);

			// Verify every certificate if they're not verified yet
			for (Certificate certif : certifs) {
				final X509Certificate x509 = certif.getX509Certificate();

				// Fetch distribution point with the URL to download the CRL
				// from
				final String CRL_DISTRIBUTION_POINTS_OID = "2.5.29.31";
				final byte[] extCoded = x509
						.getExtensionValue(CRL_DISTRIBUTION_POINTS_OID);

				final String extensionValue = new String(extCoded);
				final String url = extensionValue.substring(extensionValue
						.indexOf("http://"));
				final String filepath = url.substring(url.lastIndexOf('/') + 1);

				// Download file
				boolean fileFound = true;
				FileInputStream fis = null;
				try {
					HTTPFileDownload.download(url, filepath);
					fis = new FileInputStream(filepath);
				} catch (IOException e1) {
					// File couldn't be downloaded so verification stops
					fileFound = false;
					certif
							.setStatus(CertificateStatus.BEID_CERTSTATUS_UNABLE_TO_GET_CRL);
					allOk = false;
				}

				// Parse read data into a CRL
				if (fileFound) {
					final CertificateFactory cf = CertificateFactory
							.getInstance("X.509");
					final X509CRL crl = (X509CRL) cf.generateCRL(fis);

					// Check revoked
					if (crl.isRevoked(x509)) {
						allOk = false;
						certif
								.setStatus(CertificateStatus.BEID_CERTSTATUS_CERT_REVOKED);
					} else {
						if (certif
								.getStatus()
								.equals(
										CertificateStatus.BEID_CERTSTATUS_CERT_NOT_VALIDATED)) {
							certif
									.setStatus(CertificateStatus.BEID_CERTSTATUS_CERT_VALIDATED_OK);
						}
					}

					// Delete file
					java.io.File f = new java.io.File(filepath);
					f.delete();
				}
			}

			return allOk;
		} catch (Exception e) {
			throw new EIDException(e);
		}
	}

	/**
	 * Verifies the root certificate of the smart card.
	 * 
	 * @return whether the verification succeeded or not
	 * @throws IOException
	 *             if the file system is read only and thus the root certificate
	 *             couldn't be serialized
	 * @throws CertificateException
	 *             when the instance couldn't be parsed
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 */
	private boolean verifyRoot() throws CertificateException, IOException,
			EIDException {
		if (fEnableTestCard) {
			// Test card can't verify signatures correctly so always reply with
			// true
			return true;
		} else {
			// Verify the root certificate
		    final Certificate root = getCertificateChain().getRootCert();
			final Certificate cert = new HardCodedRootCertificate();
			final Certificate certV2 = new HardCodedRootCertificateV2();

			if (root.getX509Certificate().equals(cert.getX509Certificate()) ||
			        root.getX509Certificate().equals(certV2.getX509Certificate())) {
				return true;
			} else {
				this.getCertificateChain().getRootCert().setStatus(
						CertificateStatus.BEID_CERTSTATUS_INVALID_ROOT);
				return false;
			}
		}
	}

	/**
	 * Verifies the signature by verifying the buffer against the signature,
	 * using the given public key and the algorithm SHA1withRSA. This method is
	 * a helper class for the retrieval of certain types of data.
	 * 
	 * @param data
	 *            is the data to verify
	 * @param signature
	 *            is the signature to verify against the data
	 * @return whether the verification succeeded or not
	 * @throws CertificateException
	 *             when the instance couldn't be parsed
	 * @throws EIDException
	 *             when the operation couldn't be performed successfully, the
	 *             cause of the problem contains a more detailed description
	 * @throws IOException
	 *             is the verification failed because of IO fault
	 * @throws InvalidKeyException
	 *             when the public key is invalid
	 * @throws NoSuchAlgorithmException
	 *             when the SHA1withRSA algorithm isn't supported
	 * @throws SignatureException
	 *             when the signature is invalid
	 */
	private boolean verifyRNSignature(final byte[] data, final byte[] signature)
			throws EIDException, CertificateException, IOException,
			InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		// Verify the RRNDN of the national register certificate if card is no
		// testcard, then verify the data against the signature using the public
		// key
		if (fEnableTestCard || this.getNationalRegisterCertificate().verify()) {
			// Initialize signature with correct algorithm
			Signature sig = null;
			try {
				sig = Signature.getInstance("SHA1withRSA");
			} catch (NoSuchAlgorithmException e) {
				// Shouldn't occur
				throw e;
			}

			// Fetch public key of correct certificate
			PublicKey pk = this.getNationalRegisterCertificate()
					.getX509Certificate().getPublicKey();

			// Verify signature and return results
			sig.initVerify(pk);
			sig.update(data, 0, data.length);
			return sig.verify(signature);
		}

		// Failed
		return false;
	}

	/**
	 * Enables the prompting for the card being inserted or removed. This
	 * enables the user to plug in their own actions to execute when the card is
	 * inserted or removed.
	 * 
	 * @param cl
	 *            are the actions to occur when the card is inserted/removed
	 * @throws IllegalArgumentException
	 *             when the interval is negative
	 * 
	 */
	public void enableCardListener(final CardListener cl)
			throws IllegalArgumentException {
		fCardListener = cl;
		fCardListenThread = new CardAlivePromptTask(this, fCardListener);
		fCardListenThread.start();

	}

	/**
	 * Disables the prompting for the card being inserted or removed.
	 */
	public void disableCardListener() {
		fCardListener = null;
		fCardListenThread.stopThread();
	}

	/**
	 * Returns the card listener. Returns null if the listening is not enabled.
	 * 
	 * @return the card listener
	 */
	public CardListener getCardListener() {
		return fCardListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.belgium.eid.eidlib.SmartCard#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
	}
}