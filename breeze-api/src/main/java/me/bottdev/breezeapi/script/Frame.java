package me.bottdev.breezeapi.script;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Frame {

    private VariableContext localContext;
    private List<Instruction> instructions = new ArrayList<>();
    private int pointer = 0;

}
