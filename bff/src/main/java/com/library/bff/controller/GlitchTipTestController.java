package com.library.bff.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador utilizado para probar la integración con GlitchTip.
 */
@RestController
@RequestMapping("/test")
public class GlitchTipTestController {

    private static final Logger log =
            LoggerFactory.getLogger(GlitchTipTestController.class);

    /**
     * Genera un error intencional para comprobar los logs
     * y el envío del evento hacia GlitchTip.
     */
    @GetMapping("/glitchtip")
    public void generateGlitchTipError() {

        log.info("Iniciando prueba controlada de GlitchTip");

        throw new IllegalStateException(
                "Error de prueba controlado desde Eva-3-Library BFF"
        );
    }
}