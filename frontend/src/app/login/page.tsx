'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const res = await fetch('/api/portal/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || 'Invalid credentials or connection error');
      }

      // Store auth session
      localStorage.setItem('portal_token', data.token);
      localStorage.setItem('portal_user', JSON.stringify(data.user));

      // Redirect to overview
      router.push('/');
    } catch (err: unknown) {
      const errMsg = err instanceof Error ? err.message : 'Something went wrong';
      console.warn('Login failed:', errMsg);
      setError(errMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-[#F5F9FE]">
      {/* Left side: Branding Panel with Royal Blue PV logo */}
      <div className="hidden lg:flex w-1/2 bg-gradient-to-br from-[#005BC0] via-[#003B80] to-[#001D40] p-12 flex-col justify-between text-white relative overflow-hidden">
        {/* Glow effect */}
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-blue-400/20 rounded-full blur-[100px]" />
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-sky-400/10 rounded-full blur-[120px]" />

        <div className="z-10 flex items-center gap-3">
          <div className="bg-white p-2 rounded-lg shadow-md border border-white/10 flex items-center justify-center">
            <img src="/logo_pv.png" alt="PV Logo" className="h-9 w-auto object-contain" />
          </div>
          <div>
            <span className="text-[18px] font-bold tracking-tight block">Saksham 2.0</span>
            <span className="text-[10px] font-semibold tracking-wider text-[#9fccff] uppercase">Vikas Group</span>
          </div>
        </div>

        <div className="z-10 space-y-6 max-w-md">
          <h1 className="text-[38px] font-black tracking-tight leading-tight">
            Data Collection & Analytics Overview
          </h1>
          <p className="text-[14px] text-gray-300 font-medium leading-relaxed">
            Authorized admin gateway for monitoring, analyzing, and auditing field log reports, mechanic contributions, and points balances.
          </p>
        </div>

        <div className="z-10 text-[11px] text-gray-400">
          <span>© {new Date().getFullYear()} Vikas Group. All rights reserved.</span>
        </div>
      </div>

      {/* Right side: Login Panel with Vikas Group logo */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-[420px] bg-white border border-[#DDE1E7] p-8 rounded-xl shadow-sm transition-all duration-300">
          <div className="mb-8 flex flex-col items-center">
            <img src="/logo_vikas.png" alt="Vikas Group Logo" className="h-16 w-auto object-contain mb-4" />
            <h2 className="text-[22px] font-bold text-[#005BC0] text-center">Sign In to Portal</h2>
            <p className="text-[13px] text-[#6B6B6B] mt-1 text-center">Provide your credentials to access dashboards</p>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 text-rose-700 rounded-lg text-[13px] flex gap-2 items-start">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="flex-shrink-0 text-rose-500 mt-0.5">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label htmlFor="username" className="block text-[12px] font-semibold text-[#1A1A1A] uppercase tracking-wider mb-2">
                Username
              </label>
              <div className="relative">
                <input
                  id="username"
                  type="text"
                  placeholder="Enter administrator username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 text-[14px] bg-[#FAFBFD] border border-[#DDE1E7] rounded-lg focus:outline-none focus:border-[#005BC0] focus:ring-1 focus:ring-[#005BC0] transition-all"
                  disabled={loading}
                />
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="absolute left-3.5 top-3.5 text-[#9CA3AF]">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-[12px] font-semibold text-[#1A1A1A] uppercase tracking-wider mb-2">
                Password
              </label>
              <div className="relative">
                <input
                  id="password"
                  type="password"
                  placeholder="Enter secure password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 text-[14px] bg-[#FAFBFD] border border-[#DDE1E7] rounded-lg focus:outline-none focus:border-[#005BC0] focus:ring-1 focus:ring-[#005BC0] transition-all"
                  disabled={loading}
                />
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="absolute left-3.5 top-3.5 text-[#9CA3AF]">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-3 bg-[#005BC0] hover:bg-[#004494] disabled:bg-[#005BC0]/60 text-white rounded-lg font-bold text-[14px] flex items-center justify-center gap-2 shadow-sm transition-all duration-150 active:scale-[0.98]"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-2 border-white/30 border-t-white"></div>
                  <span>Signing In...</span>
                </>
              ) : (
                <>
                  <span>Sign In</span>
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <line x1="5" y1="12" x2="19" y2="12" />
                    <polyline points="12 5 19 12 12 19" />
                  </svg>
                </>
              )}
            </button>
          </form>

          <div className="mt-6 pt-5 border-t border-[#F0F2F5] text-center text-[11px] text-[#6B6B6B]">
            <span>Authorized access only. All activity is logged.</span>
          </div>
        </div>
      </div>
    </div>
  );
}
