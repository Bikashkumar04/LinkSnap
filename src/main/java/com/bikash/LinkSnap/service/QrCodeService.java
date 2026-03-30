package com.bikash.LinkSnap.service;

public interface QrCodeService {

    byte[] generatePng(String content, int size);
}
