package com.overeasy.smartfitness.module.tensorflowmanager

import org.tensorflow.lite.Interpreter

interface TensorFlowManager {
    fun getInterpreter(modelPath: String): Interpreter
}