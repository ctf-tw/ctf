import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { SERVER_API_URL } from '../../app.constants';
import { ProfileInfo } from './profile-info.model';
import {ContextInfo} from "./context-info.model";

@Injectable()
export class ProfileService {

    private profileInfoUrl = SERVER_API_URL + 'api/profile-info';
    private contextInfoUrl = SERVER_API_URL + 'api/context-info';

    constructor(private http: Http) { }

    getProfileInfo(): Observable<ProfileInfo> {
        return this.http.get(this.profileInfoUrl)
            .map((res: Response) => {
                const data = res.json();
                const pi = new ProfileInfo();
                pi.activeProfiles = data.activeProfiles;
                pi.ribbonEnv = data.ribbonEnv;
                pi.inProduction = data.activeProfiles.indexOf('prod') !== -1;
                pi.swaggerEnabled = data.activeProfiles.indexOf('swagger') !== -1;
                return pi;
            });
    }

    getContext(): Observable<ContextInfo> {
        return this.http.get(this.contextInfoUrl)
            .map((res: Response) => {
                const contextData = res.json();

                return contextData;
            });
    }
}
