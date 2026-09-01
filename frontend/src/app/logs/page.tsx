'use client';

import { useState, useEffect } from 'react';
import { fetchWithAuth } from '@/utils/api';

interface Entry {
  id: string;
  make: string;
  model: string;
  variant?: string;
  year: number;
  registration_number?: string;
  failure_cause: string;
  severity: string;
  odometer?: number;
  ac_usage?: string;
  prior_service_date?: string;
  notes?: string;
  photo_url_1?: string;
  photo_url_2?: string;
  photo_url_3?: string;
  gps_lat: number;
  gps_lng: number;
  mechanic_name?: string;
  mechanic_id: string;
  mechanic_workshop?: string;
  created_at: number;
}

export default function LogsList() {
  const [entries, setEntries] = useState<Entry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedPhoto, setSelectedPhoto] = useState<string | null>(null);

  const fetchEntries = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetchWithAuth('/api/entries');
      if (!res.ok) throw new Error('Failed to fetch failure logs');
      const data: Entry[] = await res.json();
      setEntries(data);
    } catch (err: unknown) {
      console.error(err);
      const errMsg = err instanceof Error ? err.message : String(err);
      setError(errMsg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchEntries();
    }, 0);
    return () => clearTimeout(timer);
  }, []);

  // Filter entries based on search input
  const filteredEntries = entries.filter((e) => {
    const term = searchTerm.toLowerCase();
    return (
      e.make.toLowerCase().includes(term) ||
      e.model.toLowerCase().includes(term) ||
      (e.variant || '').toLowerCase().includes(term) ||
      (e.registration_number || '').toLowerCase().includes(term) ||
      e.failure_cause.toLowerCase().includes(term) ||
      (e.mechanic_name || '').toLowerCase().includes(term) ||
      (e.mechanic_workshop || '').toLowerCase().includes(term)
    );
  });

  const getSeverityStyle = (severity: string) => {
    switch (severity) {
      case 'Total loss':
        return 'bg-rose-50 border-rose-200 text-rose-700';
      case 'Major':
        return 'bg-amber-50 border-amber-200 text-amber-700';
      default:
        return 'bg-emerald-50 border-emerald-200 text-emerald-700';
    }
  };

  return (
    <>
      <main className="p-6 space-y-6 flex-1 overflow-y-auto mt-[56px]">
        {/* Page Title & Search Bar */}
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-[18px] font-semibold text-navy">Failure Entries Log</h2>
            <p className="text-[11px] text-secondary mt-0.5">
              Detailed list of AC condenser failures synchronized from mobile workshops
            </p>
          </div>
          <div className="flex items-center gap-3">
            <input
              type="text"
              placeholder="Search make, model, workshop, cause..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="px-4 py-2 border border-[#DDE1E7] rounded text-[13px] w-[300px] focus:outline-none focus:ring-1 focus:ring-[#1F4E79] bg-white text-[#1A1A1A]"
            />
            <button
              onClick={fetchEntries}
              className="text-[11px] font-bold text-navy hover:underline bg-[#EAF0F8] px-3 py-2 rounded"
            >
              Refresh
            </button>
          </div>
        </div>

        {loading && (
          <div className="flex items-center justify-center h-48 bg-white border border-[#DDE1E7] rounded-md">
            <span className="text-[13px] text-secondary">Loading failure entries...</span>
          </div>
        )}

        {error && (
          <div className="flex flex-col items-center justify-center h-48 bg-rose-50 border border-rose-200 text-rose-700 p-4 rounded-md">
            <span className="text-[14px] font-semibold">Failed to Load Logs</span>
            <span className="text-[12px] text-rose-500 mt-1">{error}</span>
            <button
              onClick={fetchEntries}
              className="mt-4 px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded text-xs font-semibold"
            >
              Retry
            </button>
          </div>
        )}

        {!loading && !error && (
          <div className="bg-white border border-[#DDE1E7] rounded overflow-hidden shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full text-left table-fixed">
                <thead className="bg-[#1F4E79] text-white text-[11px] uppercase tracking-wider">
                  <tr>
                    <th className="px-4 py-3 font-semibold w-[120px]">Timestamp</th>
                    <th className="px-4 py-3 font-semibold w-[200px]">Vehicle Info</th>
                    <th className="px-4 py-3 font-semibold w-[180px]">Failure & Severity</th>
                    <th className="px-4 py-3 font-semibold w-[200px]">Mechanic & Workshop</th>
                    <th className="px-4 py-3 font-semibold w-[130px]">Odometer & AC</th>
                    <th className="px-4 py-3 font-semibold w-[220px]">Notes</th>
                    <th className="px-4 py-3 font-semibold w-[150px]">Photos</th>
                  </tr>
                </thead>
                <tbody className="text-[13px] divide-y divide-[#DDE1E7]">
                  {filteredEntries.length === 0 ? (
                    <tr>
                      <td colSpan={7} className="px-4 py-8 text-center text-secondary">
                        No failure entries matched your filters.
                      </td>
                    </tr>
                  ) : (
                    filteredEntries.map((row, idx) => {
                      const photoUrls = [row.photo_url_1, row.photo_url_2, row.photo_url_3].filter((u): u is string => !!u);
                      const formattedDate = row.created_at
                        ? new Date(row.created_at).toLocaleString('en-IN', {
                            day: '2-digit',
                            month: 'short',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                          })
                        : 'N/A';

                      return (
                        <tr
                          key={row.id}
                          className={idx % 2 === 1 ? 'bg-[#F9FAFB]' : 'bg-white'}
                        >
                          {/* Date */}
                          <td className="px-4 py-3.5 text-gray-500 font-medium leading-relaxed break-words">
                            {formattedDate}
                          </td>

                          {/* Vehicle */}
                          <td className="px-4 py-3.5 leading-relaxed">
                            <div className="font-semibold text-navy">
                              {row.make} {row.model}
                            </div>
                            <div className="text-[11px] text-gray-500">
                              {row.variant || 'Standard'} ({row.year})
                            </div>
                            {row.registration_number && (
                              <div className="text-[11px] font-mono bg-gray-100 px-1 py-0.5 inline-block rounded text-gray-600 mt-1 uppercase">
                                {row.registration_number}
                              </div>
                            )}
                          </td>

                          {/* Failure */}
                          <td className="px-4 py-3.5 leading-relaxed">
                            <div className="font-medium text-[#1A1A1A]">{row.failure_cause}</div>
                            <span
                              className={`mt-1.5 inline-block text-[10px] font-bold px-2 py-0.5 rounded border ${getSeverityStyle(
                                row.severity
                              )}`}
                            >
                              {row.severity}
                            </span>
                          </td>

                          {/* Mechanic */}
                          <td className="px-4 py-3.5 leading-relaxed">
                            <div className="font-semibold text-gray-800">{row.mechanic_name || 'Anonymous'}</div>
                            <div className="text-[11px] text-secondary leading-normal">
                              {row.mechanic_workshop || 'Unknown Workshop'}
                            </div>
                          </td>

                          {/* Odometer */}
                          <td className="px-4 py-3.5 text-right leading-relaxed">
                            <div className="font-semibold text-gray-700">
                              {row.odometer ? `${row.odometer.toLocaleString('en-IN')} km` : '--'}
                            </div>
                            <div className="text-[11px] text-gray-500">
                              AC: {row.ac_usage || 'N/A'}
                            </div>
                          </td>

                          {/* Notes */}
                          <td className="px-4 py-3.5 text-gray-600 leading-normal break-words whitespace-pre-line">
                            {row.notes || '--'}
                          </td>

                          {/* Photos */}
                          <td className="px-4 py-3.5">
                            {photoUrls.length === 0 ? (
                              <span className="text-[11px] text-secondary italic">No photos</span>
                            ) : (
                              <div className="flex flex-wrap gap-1.5">
                                {photoUrls.map((url, i) => (
                                  <img
                                    key={i}
                                    src={url}
                                    alt={`condenser-img-${i}`}
                                    onClick={() => setSelectedPhoto(url)}
                                    className="w-10 h-10 object-cover border border-[#DDE1E7] rounded hover:border-navy cursor-pointer transition-all"
                                  />
                                ))}
                              </div>
                            )}
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </main>

      {/* Lightbox / Zoom Modal */}
      {selectedPhoto && (
        <div
          className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4 animate-fade-in"
          onClick={() => setSelectedPhoto(null)}
        >
          <div className="relative max-w-3xl max-h-[85vh] bg-white p-2 rounded-lg shadow-2xl overflow-hidden flex flex-col">
            <button
              onClick={() => setSelectedPhoto(null)}
              className="absolute top-3 right-3 text-white bg-black/50 hover:bg-black/85 w-8 h-8 rounded-full flex items-center justify-center text-lg font-bold transition-all z-10"
            >
              &times;
            </button>
            <img
              src={selectedPhoto}
              alt="Zoomed failure log evidence"
              className="max-w-full max-h-[75vh] object-contain rounded"
            />
          </div>
        </div>
      )}
    </>
  );
}
