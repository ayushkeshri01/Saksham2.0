'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

interface SidebarProps {
  user: {
    username: string;
    role: string;
  } | null;
}

export default function Sidebar({ user }: SidebarProps) {
  const pathname = usePathname();

  const menuItems = [
    {
      id: 'national',
      label: 'National Overview',
      href: '/',
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="3" width="7" height="9"></rect>
          <rect x="14" y="3" width="7" height="5"></rect>
          <rect x="14" y="12" width="7" height="9"></rect>
          <rect x="3" y="16" width="7" height="5"></rect>
        </svg>
      ),
    },
    {
      id: 'model',
      label: 'Model Analysis',
      href: '/model-analysis',
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <line x1="18" y1="20" x2="18" y2="10"></line>
          <line x1="12" y1="20" x2="12" y2="4"></line>
          <line x1="6" y1="20" x2="6" y2="14"></line>
        </svg>
      ),
    },
    {
      id: 'logs',
      label: 'Logs List',
      href: '/logs',
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <line x1="8" y1="6" x2="21" y2="6"></line>
          <line x1="8" y1="12" x2="21" y2="12"></line>
          <line x1="8" y1="18" x2="21" y2="18"></line>
          <line x1="3" y1="6" x2="3.01" y2="6"></line>
          <line x1="3" y1="12" x2="3.01" y2="12"></line>
          <line x1="3" y1="18" x2="3.01" y2="18"></line>
        </svg>
      ),
    },
    {
      id: 'mechanics',
      label: 'Mechanics & Redemptions',
      href: '/mechanics',
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
      ),
    },
  ];

  // Dynamically add portal users management tab if role is superuser
  if (user?.role === 'superuser') {
    menuItems.push({
      id: 'users',
      label: 'Portal Users',
      href: '/users',
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
          <path d="M7 11V7a5 5 0 0 1 10 0v4" />
        </svg>
      ),
    });
  }

  const handleLogout = () => {
    localStorage.removeItem('portal_token');
    localStorage.removeItem('portal_user');
    window.location.href = '/login';
  };

  return (
    <aside className="fixed top-0 left-0 w-[220px] h-full bg-white border-r border-[#DDE1E7] z-50 flex flex-col justify-between">
      <div>
        <div className="h-[56px] flex items-center px-4 border-b border-[#DDE1E7] gap-2.5">
          <img src="/logo.png" alt="Logo" className="h-8 w-auto object-contain" />
          <div className="flex flex-col">
            <span className="text-[14px] font-bold text-[#005BC0] tracking-tight leading-tight">Saksham 2.0</span>
            <span className="text-[9px] font-semibold text-[#009fee] uppercase tracking-wider">Vikas Group</span>
          </div>
        </div>
        <nav className="mt-4 flex flex-col">
          {menuItems.map((item) => {
             const isActive = pathname === item.href;
             return (
               <Link
                 key={item.id}
                 href={item.href}
                 className={`px-6 py-3 text-[13px] flex items-center gap-3 transition-all ${
                   isActive
                     ? 'bg-[#EAF0F8] border-l-4 border-[#005BC0] text-[#005BC0] font-semibold'
                     : 'text-[#6B6B6B] hover:bg-[#F4F6F8] hover:text-[#1A1A1A] border-l-4 border-transparent'
                 }`}
               >
                 <span className={isActive ? 'text-[#005BC0]' : 'text-[#9CA3AF]'}>{item.icon}</span>
                 {item.label}
               </Link>
             );
           })}
        </nav>
      </div>

      <div className="p-4 border-t border-[#DDE1E7] text-[11px] text-[#6B6B6B] flex flex-col gap-2.5 bg-[#FAFBFD]">
        <div>
          <p className="font-semibold uppercase tracking-wider text-[9px] text-[#005BC0]">Vikas Data Analytics</p>
          <div className="flex items-center gap-2 mt-0.5">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            <span>Online / PostgreSQL</span>
          </div>
        </div>

        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2 px-3 py-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700 border border-rose-200 rounded font-bold transition-all active:scale-[0.98]"
        >
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          Sign Out
        </button>
      </div>
    </aside>
  );
}
