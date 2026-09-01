'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithAuth } from '@/utils/api';

interface PortalUser {
  id: number;
  username: string;
  role: string;
  created_at: string;
}

export default function UsersPage() {
  const router = useRouter();
  const [users, setUsers] = useState<PortalUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Current logged in user context
  const [currentUser, setCurrentUser] = useState<{ id?: number; username: string; role: string } | null>(null);

  // Form State
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('admin');
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState<string | null>(null);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetchWithAuth('/api/portal/users');
      
      if (res.status === 403) {
        throw new Error('Access denied. Only superusers can access this page.');
      }
      if (!res.ok) throw new Error('Failed to fetch admin users list.');
      
      const data = await res.json();
      setUsers(data);
    } catch (err: unknown) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Error loading users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Load current user from session
    if (typeof window !== 'undefined') {
      const storedUser = localStorage.getItem('portal_user');
      if (storedUser) {
        try {
          const parsed = JSON.parse(storedUser);
          setCurrentUser(parsed);
          if (parsed.role !== 'superuser') {
            setError('Access Denied. Only superusers are authorized to view this page.');
            setLoading(false);
            return;
          }
        } catch (e) {
          console.error(e);
        }
      }
    }
    fetchUsers();
  }, []);

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password || !role) {
      setFormError('Please fill in all the fields.');
      return;
    }

    if (password.length < 6) {
      setFormError('Password must be at least 6 characters long.');
      return;
    }

    try {
      setSubmitting(true);
      setFormError(null);
      setFormSuccess(null);

      const res = await fetchWithAuth('/api/portal/users', {
        method: 'POST',
        body: JSON.stringify({ username, password, role }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || 'Failed to create user.');
      }

      setFormSuccess('Portal administrator created successfully!');
      setUsername('');
      setPassword('');
      setRole('admin');
      
      // Refresh list
      fetchUsers();
    } catch (err: unknown) {
      setFormError(err instanceof Error ? err.message : 'Error creating user.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteUser = async (id: number, targetUsername: string) => {
    if (currentUser && targetUsername === currentUser.username) {
      alert('You cannot delete your own logged-in account.');
      return;
    }

    const confirmDelete = window.confirm(`Are you sure you want to delete administrator "${targetUsername}"?`);
    if (!confirmDelete) return;

    try {
      const res = await fetchWithAuth(`/api/portal/users/${id}`, {
        method: 'DELETE',
      });

      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.error || 'Failed to delete user.');
      }

      // Refresh list
      fetchUsers();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Error deleting user.');
    }
  };

  // Render Access Denied
  if (currentUser && currentUser.role !== 'superuser') {
    return (
      <main className="p-6 flex-1 flex flex-col items-center justify-center mt-[56px] min-h-[70vh]">
        <div className="text-center space-y-4 max-w-md bg-white border border-[#DDE1E7] p-8 rounded-xl shadow-sm">
          <div className="mx-auto w-12 h-12 bg-rose-50 rounded-full flex items-center justify-center border border-rose-200">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="text-rose-600">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </div>
          <h2 className="text-[18px] font-bold text-navy">Restricted Access</h2>
          <p className="text-[13px] text-secondary leading-relaxed">
            Your current account role is <strong>{currentUser.role}</strong>. Only administrators with <strong>superuser</strong> privileges can manage portal user accounts.
          </p>
          <button
            onClick={() => router.push('/')}
            className="mt-4 px-4 py-2 bg-[#005BC0] hover:bg-[#004494] text-white text-[12px] font-bold rounded shadow-sm transition-all"
          >
            Return to Dashboard
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="p-6 space-y-6 flex-1 overflow-y-auto mt-[56px]">
      <div>
        <h2 className="text-[18px] font-semibold text-navy">Portal Users Management</h2>
        <p className="text-[11px] text-secondary mt-0.5">
          View, register, and manage administrative credentials for the Saksham 2.0 web portal
        </p>
      </div>

      {error ? (
        <div className="flex flex-col items-center justify-center h-48 bg-rose-50 border border-rose-200 text-rose-700 p-4 rounded-md">
          <span className="text-[14px] font-semibold">Failed to Access Portal Users</span>
          <span className="text-[12px] text-rose-500 mt-1">{error}</span>
        </div>
      ) : (
        <div className="grid grid-cols-12 gap-6">
          {/* Create User Form - Left Side */}
          <div className="col-span-12 lg:col-span-4 bg-white border border-[#DDE1E7] p-6 rounded-lg shadow-sm h-fit">
            <h3 className="text-[14px] font-bold text-navy mb-4 uppercase tracking-wider">Create Admin Account</h3>
            
            {formError && (
              <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-[12px] rounded-lg">
                {formError}
              </div>
            )}
            {formSuccess && (
              <div className="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-[12px] rounded-lg font-medium">
                {formSuccess}
              </div>
            )}

            <form onSubmit={handleCreateUser} className="space-y-4">
              <div>
                <label htmlFor="form-username" className="block text-[11px] font-bold text-secondary uppercase mb-1.5">
                  Username
                </label>
                <input
                  id="form-username"
                  type="text"
                  placeholder="e.g. jsmith"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-3 py-2 text-[13px] bg-[#FAFBFD] border border-[#DDE1E7] rounded focus:outline-none focus:border-navy"
                  disabled={submitting}
                />
              </div>

              <div>
                <label htmlFor="form-password" className="block text-[11px] font-bold text-secondary uppercase mb-1.5">
                  Password
                </label>
                <input
                  id="form-password"
                  type="password"
                  placeholder="Min 6 characters"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-3 py-2 text-[13px] bg-[#FAFBFD] border border-[#DDE1E7] rounded focus:outline-none focus:border-navy"
                  disabled={submitting}
                />
              </div>

              <div>
                <label htmlFor="form-role" className="block text-[11px] font-bold text-secondary uppercase mb-1.5">
                  Portal Role
                </label>
                <select
                  id="form-role"
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  className="w-full px-3 py-2 text-[13px] bg-[#FAFBFD] border border-[#DDE1E7] rounded focus:outline-none focus:border-navy"
                  disabled={submitting}
                >
                  <option value="admin">Admin (Read-only Dashboard & Redeem)</option>
                  <option value="superuser">Superuser (Full Read-Write & User Mgmt)</option>
                </select>
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2.5 bg-[#005BC0] hover:bg-[#004494] disabled:bg-[#005BC0]/60 text-white rounded text-[13px] font-bold shadow-sm transition-all cursor-pointer text-center"
              >
                {submitting ? 'Creating...' : 'Register User'}
              </button>
            </form>
          </div>

          {/* Users List Table - Right Side */}
          <div className="col-span-12 lg:col-span-8 bg-white border border-[#DDE1E7] rounded-lg shadow-sm overflow-hidden">
            <div className="p-4 border-b border-[#DDE1E7] bg-[#FAFBFD]">
              <h3 className="text-[14px] font-bold text-navy">Administrative Members</h3>
            </div>
            
            {loading ? (
              <div className="text-center py-12 text-secondary text-[13px]">Loading users...</div>
            ) : (
              <table className="w-full text-left border-collapse">
                <thead className="bg-[#1F4E79] text-white text-[11px] font-semibold uppercase tracking-wider">
                  <tr>
                    <th className="px-6 py-3.5">ID</th>
                    <th className="px-6 py-3.5">Username</th>
                    <th className="px-6 py-3.5">Role</th>
                    <th className="px-6 py-3.5">Created At</th>
                    <th className="px-6 py-3.5 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="text-[13px] divide-y divide-[#DDE1E7]">
                  {users.map((u) => (
                    <tr key={u.id} className="hover:bg-[#FAFBFD] transition-colors">
                      <td className="px-6 py-4 font-mono font-medium text-secondary">{u.id}</td>
                      <td className="px-6 py-4 font-semibold text-navy">{u.username}</td>
                      <td className="px-6 py-4">
                        <span className={`px-2 py-0.5 text-[10px] font-bold uppercase rounded border ${
                          u.role === 'superuser' 
                            ? 'bg-purple-50 border-purple-200 text-purple-700' 
                            : 'bg-blue-50 border-blue-200 text-blue-700'
                        }`}>
                          {u.role}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-secondary">
                        {u.created_at ? new Date(parseInt(u.created_at, 10)).toLocaleString('en-IN') : '-'}
                      </td>
                      <td className="px-6 py-4 text-center">
                        <button
                          onClick={() => handleDeleteUser(u.id, u.username)}
                          disabled={currentUser ? u.username === currentUser.username : false}
                          className="px-2.5 py-1 text-[11px] border border-rose-200 text-rose-600 rounded bg-rose-50 hover:bg-rose-100 disabled:opacity-50 disabled:bg-gray-100 disabled:text-gray-400 disabled:border-gray-200 font-bold transition-all"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </main>
  );
}
