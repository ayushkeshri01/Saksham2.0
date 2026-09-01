'use client';

import { useState, useEffect } from 'react';
import { fetchWithAuth } from '@/utils/api';

interface Mechanic {
  id: string;
  name: string;
  workshop: string;
  mobile: string;
  city: string;
  available_points: number;
  total_points_redeemed: number;
  total_amount_redeemed: number;
  pan_number?: string;
  pan_status?: string;
  pan_name?: string;
}

interface RedemptionLog {
  id: string;
  points_redeemed: number;
  amount_redeemed: number;
  created_at: string; // BIGINT as string from PG
}

export default function MechanicsPage() {
  const [mechanics, setMechanics] = useState<Mechanic[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Selected Mechanic Details Modal
  const [selectedMech, setSelectedMech] = useState<Mechanic | null>(null);
  const [redemptions, setRedemptions] = useState<RedemptionLog[]>([]);
  const [redemptionsLoading, setRedemptionsLoading] = useState(false);
  
  // Redeem Points Form State
  const [redeemPoints, setRedeemPoints] = useState('');
  const [redeeming, setRedeeming] = useState(false);
  const [redeemError, setRedeemError] = useState<string | null>(null);
  const [redeemSuccess, setRedeemSuccess] = useState<string | null>(null);

  const fetchMechanics = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetchWithAuth('/api/portal/mechanics');
      if (!res.ok) throw new Error('Failed to fetch mechanics list');
      const data = await res.json();
      setMechanics(data);
    } catch (err: unknown) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Failed to load mechanics data');
    } finally {
      setLoading(false);
    }
  };

  const fetchRedemptionHistory = async (mechId: string) => {
    try {
      setRedemptionsLoading(true);
      setRedeemError(null);
      setRedeemSuccess(null);
      const res = await fetchWithAuth(`/api/portal/mechanics/${mechId}/redemptions`);
      if (!res.ok) throw new Error('Failed to fetch redemption logs');
      const data = await res.json();
      setRedemptions(data);
    } catch (err) {
      console.error(err);
    } finally {
      setRedemptionsLoading(false);
    }
  };

  useEffect(() => {
    fetchMechanics();
  }, []);

  const handleSelectMechanic = (mech: Mechanic) => {
    setSelectedMech(mech);
    setRedeemPoints('');
    fetchRedemptionHistory(mech.id);
  };

  const handleCloseModal = () => {
    setSelectedMech(null);
    setRedemptions([]);
    setRedeemPoints('');
    setRedeemError(null);
    setRedeemSuccess(null);
  };

  const handleProcessRedeem = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMech) return;

    const pts = parseInt(redeemPoints, 10);
    if (isNaN(pts) || pts <= 0) {
      setRedeemError('Please enter a valid positive integer.');
      return;
    }

    if (pts > selectedMech.available_points) {
      setRedeemError(`Cannot redeem ${pts} points. Mechanic has only ${selectedMech.available_points} available.`);
      return;
    }

    try {
      setRedeeming(true);
      setRedeemError(null);
      setRedeemSuccess(null);

      const res = await fetchWithAuth('/api/portal/redeem', {
        method: 'POST',
        body: JSON.stringify({
          mechanicId: selectedMech.id,
          pointsToRedeem: pts,
        }),
      });

      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.error || 'Redemption processing failed.');
      }

      setRedeemSuccess(data.message);
      
      // Update selected mechanic available points locally
      const updatedMech = {
        ...selectedMech,
        available_points: selectedMech.available_points - pts,
        total_points_redeemed: selectedMech.total_points_redeemed + pts,
        total_amount_redeemed: Number(selectedMech.total_amount_redeemed) + pts,
      };
      setSelectedMech(updatedMech);

      // Re-fetch mechanics list and history
      fetchMechanics();
      fetchRedemptionHistory(selectedMech.id);
      setRedeemPoints('');
    } catch (err: unknown) {
      setRedeemError(err instanceof Error ? err.message : 'Error processing redemption.');
    } finally {
      setRedeeming(false);
    }
  };

  // Filter mechanics based on search term
  const filteredMechanics = mechanics.filter((m) => {
    const term = searchTerm.toLowerCase();
    return (
      m.name.toLowerCase().includes(term) ||
      m.id.toLowerCase().includes(term) ||
      (m.mobile || '').includes(term) ||
      (m.city || '').toLowerCase().includes(term) ||
      (m.workshop || '').toLowerCase().includes(term)
    );
  });

  return (
    <main className="p-6 space-y-6 flex-1 overflow-y-auto mt-[56px]">
      {/* Title block */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-[18px] font-semibold text-navy">Mechanics & Redemptions</h2>
          <p className="text-[11px] text-secondary mt-0.5">
            Manage field mechanics, review their logged points, and process cash value redemptions
          </p>
        </div>
        <div className="flex items-center gap-3">
          <input
            type="text"
            placeholder="Search mechanics, cities, mobile..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="px-4 py-2 text-[13px] bg-white border border-[#DDE1E7] rounded-md w-72 focus:outline-none focus:border-[#005BC0] transition-all"
          />
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-48 bg-white border-base rounded-md">
          <span className="text-[13px] text-secondary">Loading mechanics directory...</span>
        </div>
      ) : error ? (
        <div className="flex flex-col items-center justify-center h-48 bg-rose-50 border border-rose-200 text-rose-700 p-4 rounded-md">
          <span className="text-[14px] font-semibold">Failed to Load Mechanics</span>
          <span className="text-[12px] text-rose-500 mt-1">{error}</span>
          <button 
            onClick={fetchMechanics} 
            className="mt-4 px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded text-xs font-semibold"
          >
            Retry Loading
          </button>
        </div>
      ) : (
        <div className="bg-white border-base rounded-lg overflow-hidden shadow-sm">
          <table className="w-full text-left border-collapse">
            <thead className="bg-[#1F4E79] text-white text-[11px] font-semibold uppercase tracking-wider">
              <tr>
                <th className="px-6 py-3.5">Mechanic ID</th>
                <th className="px-6 py-3.5">Name</th>
                <th className="px-6 py-3.5">Workshop</th>
                <th className="px-6 py-3.5">Mobile</th>
                <th className="px-6 py-3.5">City</th>
                <th className="px-6 py-3.5 text-center">KYC Status</th>
                <th className="px-6 py-3.5 text-right">Available Points</th>
                <th className="px-6 py-3.5 text-right">Points Redeemed</th>
                <th className="px-6 py-3.5 text-right">Redeemed Amount</th>
                <th className="px-6 py-3.5 text-center">Action</th>
              </tr>
            </thead>
            <tbody className="text-[13px] divide-y divide-[#DDE1E7]">
              {filteredMechanics.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-secondary">
                    No mechanics found matching search criteria.
                  </td>
                </tr>
              ) : (
                filteredMechanics.map((mech, idx) => (
                  <tr 
                    key={mech.id} 
                    className={`${idx % 2 === 1 ? 'bg-[#F9FAFB]' : 'bg-white'} hover:bg-[#F3F4F6] transition-colors`}
                  >
                    <td className="px-6 py-4 font-mono font-medium text-secondary">{mech.id}</td>
                    <td className="px-6 py-4 font-semibold text-navy">{mech.name}</td>
                    <td className="px-6 py-4 truncate max-w-[180px]" title={mech.workshop}>{mech.workshop || '-'}</td>
                    <td className="px-6 py-4 text-secondary">{mech.mobile || '-'}</td>
                    <td className="px-6 py-4">{mech.city || '-'}</td>
                    <td className="px-6 py-4 text-center">
                      <span className={`px-2 py-1 text-[11px] font-bold rounded-md ${
                        mech.pan_status === 'VERIFIED' 
                          ? 'bg-emerald-100 text-emerald-800' 
                          : mech.pan_status === 'PENDING'
                            ? 'bg-amber-100 text-amber-800'
                            : 'bg-rose-100 text-rose-800'
                      }`}>
                        {mech.pan_status || 'NOT_SUBMITTED'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right font-bold text-navy">
                      {mech.available_points.toLocaleString('en-IN')} pts
                    </td>
                    <td className="px-6 py-4 text-right text-secondary">
                      {mech.total_points_redeemed.toLocaleString('en-IN')} pts
                    </td>
                    <td className="px-6 py-4 text-right font-bold text-emerald-600">
                      ₹{Number(mech.total_amount_redeemed).toLocaleString('en-IN')}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <button
                        onClick={() => handleSelectMechanic(mech)}
                        className="px-3 py-1.5 bg-[#005BC0] hover:bg-[#004494] text-white rounded text-[11px] font-bold shadow-sm transition-all"
                      >
                        Details & Redeem
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Selected Mechanic Slide-over / Modal Detail */}
      {selectedMech && (
        <div className="fixed inset-0 bg-[#000000]/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white w-full max-w-[650px] rounded-xl shadow-lg border border-[#DDE1E7] overflow-hidden flex flex-col max-h-[90vh]">
            
            {/* Modal Header */}
            <div className="bg-[#1F4E79] text-white p-6 flex justify-between items-start">
              <div>
                <span className="text-[10px] font-bold tracking-widest text-[#00fee1] uppercase">Mechanic Profile & Cash Redemptions</span>
                <h3 className="text-[18px] font-bold mt-1 leading-tight">{selectedMech.name}</h3>
                <span className="text-[12px] text-gray-300 font-mono">ID: {selectedMech.id}</span>
              </div>
              <button 
                onClick={handleCloseModal}
                className="text-gray-300 hover:text-white transition-colors"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-6 overflow-y-auto space-y-6 flex-1">
              
              {/* Profile Details Cards */}
              <div className="grid grid-cols-2 gap-4">
                <div className="bg-[#F5F9FE] border border-[#DDE1E7] p-4 rounded-lg">
                  <span className="text-[10px] uppercase font-bold text-secondary">Workshop Location</span>
                  <p className="text-[13px] font-semibold text-navy mt-1">{selectedMech.workshop || '-'}</p>
                  <p className="text-[12px] text-secondary mt-0.5">{selectedMech.city || '-'}</p>
                </div>
                <div className="bg-[#F5F9FE] border border-[#DDE1E7] p-4 rounded-lg flex flex-col justify-between">
                  <div>
                    <span className="text-[10px] uppercase font-bold text-secondary">Points Balance</span>
                    <p className="text-[20px] font-black text-navy mt-1">
                      {selectedMech.available_points.toLocaleString('en-IN')}{' '}
                      <span className="text-[12px] font-medium text-secondary">pts</span>
                    </p>
                  </div>
                  <span className="text-[11px] text-secondary font-medium">Equivalent Value: <strong className="text-emerald-600">₹{selectedMech.available_points}</strong></span>
                </div>
              </div>

              {/* KYC Details Card */}
              <div className="bg-[#F5F9FE] border border-[#DDE1E7] p-4 rounded-lg space-y-2">
                <div className="flex justify-between items-center">
                  <span className="text-[10px] uppercase font-bold text-secondary">KYC Status</span>
                  <span className={`px-2.5 py-0.5 text-[10px] font-bold rounded-full ${
                    selectedMech.pan_status === 'VERIFIED' 
                      ? 'bg-emerald-100 text-emerald-800' 
                      : selectedMech.pan_status === 'PENDING'
                        ? 'bg-amber-100 text-amber-800'
                        : 'bg-rose-100 text-rose-800'
                  }`}>
                    {selectedMech.pan_status || 'NOT_SUBMITTED'}
                  </span>
                </div>
                {selectedMech.pan_status === 'VERIFIED' && (
                  <div className="grid grid-cols-2 gap-4 pt-1">
                    <div>
                      <span className="text-[10px] text-secondary">PAN Number</span>
                      <p className="text-[13px] font-semibold text-navy mt-0.5 font-mono">{selectedMech.pan_number || '-'}</p>
                    </div>
                    <div>
                      <span className="text-[10px] text-secondary">PAN Registered Name</span>
                      <p className="text-[13px] font-semibold text-navy mt-0.5 uppercase">{selectedMech.pan_name || '-'}</p>
                    </div>
                  </div>
                )}
              </div>

              {/* Redeem Points Action Form */}
              <div className="border border-[#DDE1E7] p-5 rounded-lg space-y-4">
                <h4 className="text-[13px] font-bold text-navy uppercase tracking-wider">Process Points Redemption</h4>
                
                {redeemError && (
                  <div className="p-3.5 bg-rose-50 border border-rose-200 text-rose-700 text-[12px] rounded-lg">
                    {redeemError}
                  </div>
                )}
                {redeemSuccess && (
                  <div className="p-3.5 bg-emerald-50 border border-emerald-200 text-emerald-800 text-[12px] rounded-lg font-medium">
                    {redeemSuccess}
                  </div>
                )}

                <form onSubmit={handleProcessRedeem} className="flex gap-4 items-end">
                  <div className="flex-1">
                    <label htmlFor="redeem-pts-input" className="block text-[11px] font-bold text-secondary uppercase mb-1.5">
                      Points to Redeem
                    </label>
                    <div className="relative">
                      <input
                        id="redeem-pts-input"
                        type="number"
                        placeholder="Min 100 points"
                        value={redeemPoints}
                        onChange={(e) => setRedeemPoints(e.target.value)}
                        className="w-full pl-3 pr-10 py-2 text-[14px] border border-[#DDE1E7] rounded focus:outline-none focus:border-navy"
                        disabled={redeeming || selectedMech.available_points < 100}
                      />
                      <span className="absolute right-3 top-2.5 text-[11px] font-bold text-[#6B6B6B]">
                        = ₹{redeemPoints ? Number(redeemPoints) * 1 : 0}
                      </span>
                    </div>
                  </div>
                  <button
                    type="submit"
                    disabled={redeeming || selectedMech.available_points < 100 || !redeemPoints || selectedMech.pan_status !== 'VERIFIED'}
                    className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-[#DDE1E7] text-white text-[13px] font-bold rounded shadow-sm transition-colors cursor-pointer"
                  >
                    {redeeming ? 'Redeeming...' : 'Confirm Cash Release'}
                  </button>
                </form>
                {selectedMech.available_points < 100 && (
                  <p className="text-[11px] text-rose-500 font-semibold mt-1">
                    * Minimum redemption balance is 100 points.
                  </p>
                )}
                {selectedMech.pan_status !== 'VERIFIED' && (
                  <p className="text-[11px] text-rose-500 font-semibold mt-1">
                    * KYC verification is required to process redemptions for this mechanic.
                  </p>
                )}
              </div>

              {/* Transaction Logs */}
              <div className="space-y-3">
                <h4 className="text-[13px] font-bold text-navy uppercase tracking-wider">Redemption Transactions Log</h4>
                
                {redemptionsLoading ? (
                  <div className="text-center py-6 text-[12px] text-secondary">Loading transactions...</div>
                ) : redemptions.length === 0 ? (
                  <div className="text-center py-8 text-[12px] text-secondary border border-dashed border-[#DDE1E7] rounded-lg">
                    No redemption transactions processed for this mechanic.
                  </div>
                ) : (
                  <div className="border border-[#DDE1E7] rounded-lg overflow-hidden">
                    <table className="w-full text-left">
                      <thead className="bg-[#FAFBFD] text-[10px] uppercase font-bold text-secondary border-b border-[#DDE1E7]">
                        <tr>
                          <th className="px-4 py-2">Transaction ID</th>
                          <th className="px-4 py-2">Points Released</th>
                          <th className="px-4 py-2 text-right">Cash Amount</th>
                          <th className="px-4 py-2 text-right">Processed At</th>
                        </tr>
                      </thead>
                      <tbody className="text-[12px] divide-y divide-[#DDE1E7]">
                        {redemptions.map((log) => (
                          <tr key={log.id} className="hover:bg-[#FAFBFD]">
                            <td className="px-4 py-2.5 font-mono text-[10px] text-secondary">{log.id.slice(0, 8)}...</td>
                            <td className="px-4 py-2.5 font-semibold text-navy">{log.points_redeemed} pts</td>
                            <td className="px-4 py-2.5 text-right font-bold text-emerald-600">₹{Number(log.amount_redeemed).toLocaleString('en-IN')}</td>
                            <td className="px-4 py-2.5 text-right text-secondary">
                              {new Date(parseInt(log.created_at, 10)).toLocaleString('en-IN')}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>

            {/* Modal Footer */}
            <div className="border-t border-[#DDE1E7] p-4 bg-[#FAFBFD] flex justify-end">
              <button
                onClick={handleCloseModal}
                className="px-4 py-2 border border-[#DDE1E7] hover:bg-[#F3F4F6] text-secondary text-[13px] font-bold rounded shadow-sm transition-colors"
              >
                Close Profile
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
