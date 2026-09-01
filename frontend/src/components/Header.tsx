'use client';

import { usePathname } from 'next/navigation';

interface HeaderProps {
  user: {
    username: string;
    role: string;
  } | null;
}

export default function Header({ user }: HeaderProps) {
  const pathname = usePathname();

  const getPageTitle = () => {
    switch (pathname) {
      case '/':
        return 'National Overview';
      case '/model-analysis':
        return 'Model Analysis';
      case '/logs':
        return 'Failure Entries Log';
      case '/mechanics':
        return 'Mechanics & Redemptions';
      case '/users':
        return 'Portal Users Management';
      default:
        return 'Saksham 2.0 Dashboard';
    }
  };

  return (
    <header className="fixed top-0 right-0 left-[220px] h-[56px] bg-white border-b border-base flex items-center justify-between px-6 z-40">
      <h1 className="text-[15px] font-semibold text-navy transition-all duration-200">
        {getPageTitle()}
      </h1>
      <div className="flex items-center gap-5">
        {/* Android APK Download Action Button */}
        <a
          href="/app-debug.apk"
          download="saksham-app.apk"
          className="bg-[#005BC0] hover:bg-[#004494] text-white text-[12px] font-bold px-3.5 py-1.5 rounded flex items-center gap-2 shadow-sm transition-all duration-150 hover:scale-[1.02] active:scale-[0.98]"
        >
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
            <polyline points="7 10 12 15 17 10"></polyline>
            <line x1="12" y1="15" x2="12" y2="3"></line>
          </svg>
          Download Mobile App
        </a>

        {/* User Identity Section */}
        <div className="text-[13px] text-secondary flex items-center gap-1.5">
          <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full"></span>
          <span>
            <strong className="text-[#1A1A1A] capitalize">{user?.username || 'Admin'}</strong> —{' '}
            <span className="capitalize">{user?.role || 'Administrator'}</span>
          </span>
        </div>
      </div>
    </header>
  );
}
