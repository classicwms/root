import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-customdialog2',
  templateUrl: './customdialog2.component.html',
  styleUrls: ['./customdialog2.component.scss']
})
export class Customdialog2Component implements OnInit {
  form = this.fb.group({
   remarks:[],
  });
  constructor(public dialogRef: MatDialogRef<Customdialog2Component>,private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  
  this.fill();
  }
  onNoClick() { this.dialogRef.close(this.data); }
fill(){
console.log(this.data);
if(this.data.value != null){
  this.form.controls.remarks.patchValue(this.data.value);
}
}
cancel(){
  console.log(this.data);
  if(this.data.value !=null){
  this.data.remarks = this.form.controls.remarks.value;
  this.data.button="cancel";
  this.dialogRef.close(this.data); 
  
  }
  else{
    this.dialogRef.close();
  }
}
  submit(){
    this.data.remarks = this.form.controls.remarks.value;

    
    this.dialogRef.close(this.data);
   // this.dialogRef.close(this.data.remarks);
  }
  
}

