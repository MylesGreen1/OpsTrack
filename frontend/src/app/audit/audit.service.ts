import { API_BASE_URL } from '../api.config';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
export interface AuditLog { id:number; username:string; action:string; resourceType:string; resourceId:string|null; details:string; createdAt:string; }
@Injectable({providedIn:'root'}) export class AuditService { private readonly apiUrl=`${API_BASE_URL}/api/audit-logs`; constructor(private readonly http:HttpClient){} getAll():Observable<AuditLog[]>{return this.http.get<AuditLog[]>(this.apiUrl);} }
