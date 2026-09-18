import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AuditLog, AuditService } from './audit.service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './audit.html',
  styleUrl: './audit.css',
})
export class AuditComponent implements OnInit {
  logs: AuditLog[] = [];

  loading = true;

  errorMessage = '';

  constructor(
    private readonly auditService: AuditService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs(): void {
    this.loading = true;
    this.errorMessage = '';

    this.auditService.getAll().subscribe({
      next: (logs: AuditLog[]) => {
        this.logs = [...logs].sort((firstLog, secondLog) =>
          secondLog.createdAt.localeCompare(firstLog.createdAt),
        );

        this.loading = false;

        this.changeDetectorRef.markForCheck();
      },

      error: (error: unknown) => {
        console.error('Error loading audit history:', error);

        this.errorMessage = 'Unable to load audit history.';

        this.loading = false;

        this.changeDetectorRef.markForCheck();
      },
    });
  }
}
