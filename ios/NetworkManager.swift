import Foundation
import UIKit

class NetworkManager {
    static let shared = NetworkManager()
    
    // Configurable API URL (default matches local Android client configuration)
    var baseURL = "http://localhost:5000"
    
    private init() {}
    
    func register(payload: MechanicPayload) async throws {
        guard let url = URL(string: "\(baseURL)/api/mechanics") else {
            throw URLError(.badURL)
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let encoder = JSONEncoder()
        request.httpBody = try encoder.encode(payload)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw URLError(.badServerResponse)
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            let errorText = String(data: data, encoding: .utf8) ?? "HTTP \(httpResponse.statusCode)"
            throw NSError(domain: "NetworkError", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: errorText])
        }
    }
    
    func login(payload: LoginPayload) async throws -> MechanicProfile {
        guard let url = URL(string: "\(baseURL)/api/mechanics/login") else {
            throw URLError(.badURL)
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let encoder = JSONEncoder()
        request.httpBody = try encoder.encode(payload)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw URLError(.badServerResponse)
        }
        
        guard httpResponse.statusCode == 200 else {
            let errorText = String(data: data, encoding: .utf8) ?? "HTTP \(httpResponse.statusCode)"
            throw NSError(domain: "NetworkError", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: errorText])
        }
        
        let decoder = JSONDecoder()
        let result = try decoder.decode(LoginResponse.self, from: data)
        return result.mechanic
    }
    
    func syncJob(entry: JobEntry) async throws {
        let isCompressor = entry.componentType == "compressor"
        let endpoint = isCompressor ? "/api/compressor/sync" : "/api/sync"
        
        guard let url = URL(string: "\(baseURL)\(endpoint)") else {
            throw URLError(.badURL)
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let payload = SyncPayload(
            id: entry.id,
            make: entry.make,
            model: entry.model,
            variant: entry.variant,
            year: entry.year,
            registrationNumber: entry.registrationNumber,
            photoBase64_1: imagePathToBase64(entry.photoPath1),
            photoBase64_2: imagePathToBase64(entry.photoPath2),
            photoBase64_3: imagePathToBase64(entry.photoPath3),
            gpsLatitude: entry.gpsLatitude,
            gpsLongitude: entry.gpsLongitude,
            timestamp: entry.timestamp,
            failureCause: entry.failureCause,
            severity: entry.severity,
            odometer: entry.odometer,
            acUsage: entry.acUsage,
            priorServiceDate: entry.priorServiceDate,
            notes: entry.notes,
            mechanicId: entry.mechanicId,
            createdAt: entry.createdAt
        )
        
        let encoder = JSONEncoder()
        request.httpBody = try encoder.encode(payload)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw URLError(.badServerResponse)
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            let errorText = String(data: data, encoding: .utf8) ?? "HTTP \(httpResponse.statusCode)"
            throw NSError(domain: "NetworkError", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: errorText])
        }
    }
    
    private func imagePathToBase64(_ path: String?) -> String? {
        guard let path = path, !path.isEmpty else { return nil }
        guard let image = UIImage(contentsOfFile: path) else { return nil }
        guard let imageData = image.jpegData(compressionQuality: 0.7) else { return nil }
        return imageData.base64EncodedString()
    }
}
