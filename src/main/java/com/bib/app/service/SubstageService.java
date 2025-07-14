package com.bib.app.service;

import com.bib.app.repository.SubstageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubstageService implements ISubstageService{
    private  final SubstageRepository substageRepository;
}
