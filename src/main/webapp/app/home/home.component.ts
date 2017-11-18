import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Account, LoginModalService, Principal } from '../shared';
import { FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.scss'
    ]

})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    form: FormGroup;
    loading: boolean = false;
    fileUploaded: boolean = false;
    name: string = 'none';

    @ViewChild('fileInput') fileInput: ElementRef;

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private fb: FormBuilder,
        private http: HttpClient
    ) {
        this.createForm();
    }

    createForm() {
        this.form = this.fb.group({
            avatar: null
        });
    }


    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
            });
        });
    }

    onFileChange(event) {
        if(event.target.files && event.target.files.length > 0) {
            let file = event.target.files[0];
            console.log("FILE");
            console.log(file);
            let formData:FormData = new FormData();
            formData.append('file', file, file.name);

            this.http.post("http://localhost:8080/files/upload", formData)
                .map((res: Response) => res.json())
                .catch(error => Observable.throw(error))
                .subscribe(
                    data => console.log('success'),
                    error => console.log(error)
                );
            this.fileUploaded = true;
            this.name = file.name;
        }
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    fileChanged() {
        return this.fileUploaded;
    }
}
