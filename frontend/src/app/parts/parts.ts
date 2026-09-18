import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { MaintenanceTask, MaintenanceTaskService } from '../maintenance/maintenance-task.service';
import { PartRequest, PartRequestCreate, PartRequestService, PartRequestStatus } from './part-request.service';

@Component({ selector: 'app-parts', standalone: true, imports: [FormsModule, RouterLink], templateUrl: './parts.html', styleUrl: './parts.css' })
export class PartsComponent implements OnInit {
  requests: PartRequest[] = [];
  tasks: MaintenanceTask[] = [];
  taskId: number | null = null;
  partNumber = '';
  partName = '';
  quantity = 1;
  notes = '';
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(private readonly parts: PartRequestService,
              private readonly tasksService: MaintenanceTaskService,
              private readonly auth: AuthService,
              private readonly cdr: ChangeDetectorRef) {}

  ngOnInit(): void { this.load(); this.tasksService.getAllTasks().subscribe({ next: v => { this.tasks = v; this.cdr.markForCheck(); } }); }
  get canManageStatus(): boolean { return this.auth.hasRole('SUPERVISOR', 'ADMIN'); }
  get canCreate(): boolean { return this.auth.hasRole('TECHNICIAN'); }

  load(): void {
    this.loading = true; this.errorMessage = '';
    const source = this.auth.hasRole('TECHNICIAN') ? this.parts.getMyRequests() : this.parts.getAll();
    source.subscribe({ next: v => { this.requests = v; this.loading = false; this.cdr.markForCheck(); }, error: () => { this.errorMessage = 'Unable to load part requests.'; this.loading = false; this.cdr.markForCheck(); } });
  }

  submit(): void {
    if (!this.taskId || !this.partNumber.trim() || !this.partName.trim() || this.quantity < 1) { this.errorMessage = 'Task, part number, part name, and a valid quantity are required.'; return; }
    const request: PartRequestCreate = { partNumber: this.partNumber.trim(), partName: this.partName.trim(), quantity: this.quantity, notes: this.notes.trim() };
    this.saving = true; this.errorMessage = ''; this.successMessage = '';
    this.parts.create(this.taskId, request).subscribe({ next: () => { this.partNumber=''; this.partName=''; this.quantity=1; this.notes=''; this.taskId=null; this.saving=false; this.successMessage='Part request submitted.'; this.load(); }, error: () => { this.saving=false; this.errorMessage='Unable to submit part request.'; this.cdr.markForCheck(); } });
  }

  setStatus(request: PartRequest, status: PartRequestStatus): void {
    this.parts.updateStatus(request.id, status).subscribe({ next: updated => { this.requests = this.requests.map(v => v.id === updated.id ? updated : v); this.cdr.markForCheck(); }, error: () => { this.errorMessage='Unable to update part request.'; this.cdr.markForCheck(); } });
  }
}
