package at.asitplus.regkassen.core.modules.signature.jws;

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.SignatureModule;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * JWS Signature module based on JWS library http://connect2id.com/products/nimbus-jose-jwt
 */
public class OtherJWSModule implements JWSModule {

    protected SignatureModule signatureModule;
    protected boolean damaged = false;
    protected JWSSigner jwsSigner;

    @Override
    public void setSignatureModule(SignatureModule signatureModule) {
        this.signatureModule = signatureModule;
        ECPrivateKey key = (ECPrivateKey)signatureModule.getSigningKey();
        try {
            this.jwsSigner = new ECDSASigner(key);
        } catch (JOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public SignatureModule getSignatureModule() {
        return signatureModule;
    }

    @Override
    public String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt,
                                              RKSuite rkSuite) {

        try {
            // Creates the JWS object with payload
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.parse(rkSuite.getJwsSignatureAlgorithm())), new Payload(machineCodeRepOfReceipt));

            // Compute the EC signature
            jwsObject.sign(jwsSigner);

            // Serialize the JWS to compact form
            String s = jwsObject.serialize();
            return s;
        } catch (JOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


    }

    public List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, RKSuite rkSuite) {
        List<String> signedReceipts = new ArrayList<>();
        for (String receiptRepresentationForSignature : machineCodeRepOfReceiptList) {
            signedReceipts.add(signMachineCodeRepOfReceipt(receiptRepresentationForSignature, rkSuite));
        }
        return signedReceipts;
    }

    /**
     * set damaged flag, only for demonstration purposes
     *
     * @param damaged set damaged state of signature module
     */
    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

}
