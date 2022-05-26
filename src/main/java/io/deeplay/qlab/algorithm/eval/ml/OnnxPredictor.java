package io.deeplay.qlab.algorithm.eval.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.util.Collections;

public class OnnxPredictor {
    private static final String inputName = "input_1";
    private final OrtEnvironment env = OrtEnvironment.getEnvironment();
    private final OrtSession session;

    public OnnxPredictor(String path) throws OrtException {
        session = env.createSession(path);
    }

    public float[] predict(float[] inp) throws OrtException {
        OnnxTensor test = OnnxTensor.createTensor(env, inp);
        OrtSession.Result output = session.run(Collections.singletonMap(inputName, test));
        return (float[]) output.get(0).getValue();
    }
}
