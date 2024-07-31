package com.overeasy.smartfitness.module.tensorflowmanager

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

class TensorFlowManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TensorFlowManager {
    override fun getInterpreter(modelPath: String): Interpreter {
        val fileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        val model = fileInputStream.channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(model)
    }
}