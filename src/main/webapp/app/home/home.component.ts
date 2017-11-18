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
        let reader = new FileReader();
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


            // reader.readAsDataURL(file);
            // reader.onload = () => {
            //     this.form.get('avatar').setValue({
            //         filetype: file.type,
            //         value: reader.result.split(',')[1],
            //         file: file
            //     })
            // };
        }
    }

    onSubmit() {
        const formModel = this.form.value;
        this.loading = true;
        // In a real-world app you'd have a http request / service call here like
        // this.http.post('apiUrl', formModel)
        setTimeout(() => {
            console.log(formModel);
            let formData:FormData = new FormData();
            alert('done!');
            this.loading = false;
        }, 1000);
    }

    clearFile() {
        this.form.get('avatar').setValue(null);
        this.fileInput.nativeElement.value = '';
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
