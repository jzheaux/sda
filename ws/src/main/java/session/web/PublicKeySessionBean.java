package session.web;

import org.springframework.stereotype.Service;

import regression.CacheKeys;
import regression.Regression;
import regression.User;
import cryptosystem.PaillierPublicKey;

@Service
public class PublicKeySessionBean {

    public void handlePublicKey(User user, PaillierPublicKey publicKey) {
        Regression.putCacheContent(CacheKeys.keyForPublicKey(user), publicKey);
    }
}
