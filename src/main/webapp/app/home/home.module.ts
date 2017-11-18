import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CtfSharedModule } from '../shared';

import { HOME_ROUTE, HomeComponent } from './';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";

@NgModule({
    imports: [
        CtfSharedModule,
        RouterModule.forRoot([ HOME_ROUTE ], { useHash: true }),
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
    ],
    declarations: [
        HomeComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CtfHomeModule {}
