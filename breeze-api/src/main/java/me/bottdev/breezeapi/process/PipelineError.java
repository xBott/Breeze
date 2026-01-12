package me.bottdev.breezeapi.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PipelineError {

    private final PipelineNode node;
    private final String message;
    private final boolean critical;

}
