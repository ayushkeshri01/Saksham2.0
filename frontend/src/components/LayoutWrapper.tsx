'use client';

import { useEffect, useState } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import Sidebar from '@/components/Sidebar';
import Header from '@/components/Header';

interface PortalUser {
  username: string;
  role: string;
}

export default function LayoutWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [user, setUser] = useState<PortalUser | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('portal_token');
    const storedUser = localStorage.getItem('portal_user');
    
    if (!token || !storedUser) {
      setIsAuthenticated(false);
      if (pathname !== '/login') {
        router.push('/login');
      }
    } else {
      setIsAuthenticated(true);
      try {
        setUser(storedUser ? JSON.parse(storedUser) : null);
      } catch (err) {
        console.error("Failed to parse user session:", err);
      }
      if (pathname === '/login') {
        router.push('/');
      }
    }
  }, [pathname, router]);

  // Loading state while checking auth
  if (isAuthenticated === null) {
    return (
      <div className="flex h-full min-h-screen items-center justify-center bg-[#F5F9FE]">
        <div className="flex flex-col items-center gap-4">
          <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-[#005BC0]"></div>
          <span className="text-[12px] font-semibold text-[#005BC0] tracking-wider uppercase">Authenticating Portal...</span>
        </div>
      </div>
    );
  }

  // Render Login page clean (without headers and sidebar)
  if (pathname === '/login') {
    return <>{children}</>;
  }

  return (
    <div className="flex min-h-screen bg-[#F4F6F8]">
      <Sidebar user={user} />
      <div className="flex-1 ml-[220px] flex flex-col min-h-screen">
        <Header user={user} />
        <div className="flex-1 flex flex-col">
          {children}
        </div>
      </div>
    </div>
  );
}
