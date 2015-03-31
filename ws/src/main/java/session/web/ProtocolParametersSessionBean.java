package session.web;

import org.springframework.stereotype.Service;

import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;

@Service
public class ProtocolParametersSessionBean {

    public void handleProtocolParameters(ProtocolParameters parameters) {
        Regression.putCacheContent(CacheKeys.PROTOCOL_PARAMETERS, parameters);
    }
}
