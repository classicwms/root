import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/core/core';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  lgForm: any;
  error: any;
  hide = true;
  title = environment.title;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    public dialog: MatDialog,
    private auth: AuthService,
    public toastr: ToastrService
  ) { }
  ngOnInit() {
    localStorage.clear();
    sessionStorage.clear();

    this.inForm();
  }

  inForm() {
    this.lgForm = this.fb.group({
      userName: ["", Validators.required],
      password: ["", Validators.required]
    });

  }
  login() {
    localStorage.clear();
    sessionStorage.clear();
    // stop here if form is invalid
    if (this.lgForm.invalid) {
      this.error = "Please fill required fields to continue";

      this.toastr.error(
        "Please fill required fields to continue",
        "Username/Password"
      );
      return;
    }
    this.auth.login(this.lgForm.value);

  }
}
