package br.com.devlucasyuji.camposer.state

import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.compose.runtime.Immutable

/**
 * Intermediate Image analyzer from cameraX
 *
 * @param imageAnalysisBackpressureStrategy the backpressure strategy applied to the image producer
 * @param imageAnalysisTargetSize the intended output size for ImageAnalysis
 * @param imageAnalysisImageQueueDepth the image queue depth of ImageAnalysis.
 * @param analyzerCallback receive images and perform custom processing.
 *
 * @see rememberImageAnalyzer
 * */
@Immutable
class ImageAnalyzer(
    private val cameraState: CameraState,
    imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy,
    imageAnalysisTargetSize: Size?,
    imageAnalysisImageQueueDepth: Int,
    private var analyzerCallback: (ImageProxy) -> Unit,
) {

    /**
     * Hold Image analysis Analyzer to camera.
     * */
    internal val analyzer: ImageAnalysis.Analyzer = Analyzer()

    init {
        updateCameraState(
            imageAnalysisBackpressureStrategy,
            imageAnalysisTargetSize?.let { CameraController.OutputSize(it) },
            imageAnalysisImageQueueDepth
        )
    }

    private fun updateCameraState(
        imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy,
        imageAnalysisTargetSize: CameraController.OutputSize?,
        imageAnalysisImageQueueDepth: Int,
    ) = with(cameraState) {
        this.imageAnalysisBackpressureStrategy = imageAnalysisBackpressureStrategy.strategy
        this.imageAnalysisTargetSize = imageAnalysisTargetSize
        this.imageAnalysisImageQueueDepth = imageAnalysisImageQueueDepth
    }

    @Immutable
    private inner class Analyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            this@ImageAnalyzer.analyzerCallback(image)
        }
    }

    /**
     * Update actual image analysis instance.
     * */
    fun update(
        imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy = ImageAnalysisBackpressureStrategy.find(
            cameraState.imageAnalysisBackpressureStrategy
        ),
        imageAnalysisTargetSize: Size? = cameraState.imageAnalysisTargetSize?.resolution,
        imageAnalysisImageQueueDepth: Int = cameraState.imageAnalysisImageQueueDepth,
        analyzerCallback: (ImageProxy) -> Unit = this.analyzerCallback,
    ) {
        updateCameraState(
            imageAnalysisBackpressureStrategy,
            imageAnalysisTargetSize?.let { CameraController.OutputSize(it) },
            imageAnalysisImageQueueDepth
        )
        this.analyzerCallback = analyzerCallback
    }
}