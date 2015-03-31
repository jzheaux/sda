package cryptosystem;

public class PaillierKey {

    private PaillierPrivateKey privateKey;
    private PaillierPublicKey publicKey;

    public PaillierKey(PaillierPrivateKey privateKey, PaillierPublicKey publicKey) {
        this.setPrivateKey(privateKey);
        this.setPublicKey(publicKey);
    }

    public PaillierKey() {
        PaillierKey key = Paillier.generateKey();
        this.setPrivateKey(key.getPrivateKey());
        this.setPublicKey(key.getPublicKey());
    }

    public PaillierPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PaillierPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PaillierPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PaillierPublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
