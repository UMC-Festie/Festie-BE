package com.umc.FestieBE.domain.view.api;

import com.umc.FestieBE.domain.view.application.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewController {
    private ViewService viewService;

}
