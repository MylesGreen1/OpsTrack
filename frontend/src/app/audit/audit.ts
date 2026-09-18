import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuditLog, AuditService } from './audit.service';
@Component({selector:'app-audit',standalone:true,imports:[RouterLink,DatePipe],templateUrl:'./audit.html',styleUrl:'./audit.css'})
export class AuditComponent implements OnInit { logs:AuditLog[]=[]; loading=true; errorMessage=''; constructor(private readonly service:AuditService,private readonly cdr:ChangeDetectorRef){} ngOnInit():void{this.load();} load():void{this.loading=true;this.service.getAll().subscribe({next:v=>{this.logs=[...v].sort((a,b)=>b.createdAt.localeCompare(a.createdAt));this.loading=false;this.cdr.markForCheck();},error:()=>{this.errorMessage='Unable to load audit history.';this.loading=false;this.cdr.markForCheck();}});} }
