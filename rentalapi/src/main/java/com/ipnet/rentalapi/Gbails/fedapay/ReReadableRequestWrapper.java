package com.ipnet.rentalapi.Gbails.fedapay;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class ReReadableRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public ReReadableRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.cachedBody = body;
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            public int read() { return bais.read(); }
            public boolean isFinished() { return bais.available() == 0; }
            public boolean isReady() { return true; }
            public void setReadListener(ReadListener l) {}
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    // ← Méthode clé pour récupérer le body dans le controller
    public byte[] getCachedBody() {
        return cachedBody;
    }
}