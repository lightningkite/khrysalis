

//
//  QRScannerView.swift
//  QRCodeReader
//
//  Created by KM, Abhilash a on 08/03/19.
//  Copyright © 2019 KM, Abhilash. All rights reserved.
//

import Foundation
import UIKit
import AVFoundation
import RxSwift
import Khrysalis

/// Delegate callback for the QRScannerView.
protocol QRScannerViewDelegate {
    func qrScanningDidFail()
    func qrScanningSucceededWithCode(_ str: String?)
    func qrScanningDidStop()
}

public class BarcodeScannerView: UIView {
    
    var delegate: QRScannerViewDelegate?
    
    /// capture settion which allows us to start and stop scanning.
    var captureSession: AVCaptureSession?
    
    // Init methods..
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        doInitialSetup()
    }
    public override init(frame: CGRect) {
        super.init(frame: frame)
        doInitialSetup()
    }
    
    //MARK: overriding the layerClass to return `AVCaptureVideoPreviewLayer`.
    public override class var layerClass: AnyClass  {
        return AVCaptureVideoPreviewLayer.self
    }
    public override var layer: AVCaptureVideoPreviewLayer {
        return super.layer as! AVCaptureVideoPreviewLayer
    }
}
extension BarcodeScannerView {
    
    var isRunning: Bool {
        return captureSession?.isRunning ?? false
    }
    
    func startScanning(delegate: QRScannerViewDelegate) {
        self.delegate = delegate
        captureSession?.startRunning()
    }
    
    func stopScanning() {
        captureSession?.stopRunning()
        delegate?.qrScanningDidStop()
    }
    
    /// Does the initial setup for captureSession
    private func doInitialSetup() {
        clipsToBounds = true
        captureSession = AVCaptureSession()
        
        guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else { return }
        let videoInput: AVCaptureDeviceInput
        do {
            videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
        } catch let error {
            print(error)
            return
        }
        
        if (captureSession?.canAddInput(videoInput) ?? false) {
            captureSession?.addInput(videoInput)
        } else {
            scanningDidFail()
            return
        }
        
        let metadataOutput = AVCaptureMetadataOutput()
        
        if (captureSession?.canAddOutput(metadataOutput) ?? false) {
            captureSession?.addOutput(metadataOutput)
            
            metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            metadataOutput.metadataObjectTypes = [.qr, .ean8, .ean13, .pdf417]
        } else {
            scanningDidFail()
            return
        }
        
        self.layer.session = captureSession
        self.layer.videoGravity = .resizeAspectFill
        
        captureSession?.startRunning()
    }
    func scanningDidFail() {
        delegate?.qrScanningDidFail()
        captureSession = nil
    }
    
    func found(code: String) {
        delegate?.qrScanningSucceededWithCode(code)
    }
    
}

extension BarcodeScannerView: AVCaptureMetadataOutputObjectsDelegate {
    public func metadataOutput(_ output: AVCaptureMetadataOutput,
                        didOutput metadataObjects: [AVMetadataObject],
                        from connection: AVCaptureConnection) {
        
        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
            guard let stringValue = readableObject.stringValue else { return }
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
            found(code: stringValue)
        }
    }
    
}

struct ScannerDelegateHelper:QRScannerViewDelegate{
    var didFail: () -> ()
    var didSuccess: (String) -> ()
    var didStop: () -> ()
    func qrScanningDidFail() {
        didFail()
    }
    
    func qrScanningSucceededWithCode(_ str: String?) {
        if let str = str {
            didSuccess(str)
        }
    }
    
    func qrScanningDidStop() {
        didStop()
    }
}


public extension BarcodeScannerView{
    
    func bindBarcodeScan(dependency: ViewDependency) -> Observable<String> {
        let out = PublishSubject<String>.create()
        
        self.startScanning(delegate: ScannerDelegateHelper(didFail: {}, didSuccess: {(str) in out.onNext(str)}, didStop: {}))
        
        return out
    }
}
