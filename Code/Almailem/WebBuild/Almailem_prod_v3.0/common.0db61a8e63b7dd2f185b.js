(self.webpackChunkclassic_wms_angular_ui=self.webpackChunkclassic_wms_angular_ui||[]).push([[592],{1786:(e,t,s)=>{"use strict";s.d(t,{I:()=>u});var r=s(7716),a=s(1841),n=s(8309);let u=(()=>{class e{constructor(e,t){this.http=e,this.auth=t}Getall(){return this.http.get("/wms-idmaster-service/usermanagement")}Get(e,t,s,r,a,n){return this.http.get("/wms-idmaster-service/usermanagement/"+e+"?warehouseId="+t+"&companyCode="+s+"&languageId="+r+"&plantId="+a+"&userRoleId="+n)}Create(e){return this.http.post("/wms-idmaster-service/usermanagement",e)}Update(e,t,s,r,a,n,u){return this.http.patch("/wms-idmaster-service/usermanagement/"+t+"?warehouseId="+s+"&companyCode="+r+"&languageId="+a+"&plantId="+n+"&userRoleId="+u,e)}Delete(e,t,s,r,a,n){return this.http.delete("/wms-idmaster-service/usermanagement/"+e+"?warehouseId="+t+"&companyCode="+s+"&languageId="+r+"&plantId="+a+"&userRoleId="+n)}search(e){return this.http.post("/wms-idmaster-service/usermanagement/findUserManagement",e)}findProfile(e){return this.http.post("/wms-idmaster-service/usermanagement/findUserManagement",e)}}return e.\u0275fac=function(t){return new(t||e)(r.LFG(a.eN),r.LFG(n.e))},e.\u0275prov=r.Yz7({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()}}]);