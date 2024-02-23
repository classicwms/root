import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-requestpopup',
  templateUrl: './requestpopup.component.html',
  styleUrls: ['./requestpopup.component.scss']
})
export class RequestpopupComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<RequestpopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
    console.log(this.data);
  }
  cancel(){
    this.dialogRef.close();
  }
  downloadJsonAsTextFile() {
    // Convert the JSON object to a string
    const jsonString = JSON.stringify(this.data.value, null, 2);

    // Create a Blob with the JSON string
    const blob = new Blob([jsonString], { type: 'text/plain' });

    // Create a temporary anchor element
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
if(this.data.type!="Outbound"){
    // Set the filename for the downloaded file
    a.download = `InboundOrder_${this.data.value[0]?.refDocumentNo}.txt`;
}
else{
  a.download = `OutboundOrder_${this.data.value[0]?.refDocumentNo}.txt`;
}

    // Append the anchor element to the body
    document.body.appendChild(a);

    // Trigger a click on the anchor element to start the download
    a.click();

    // Remove the anchor element from the body
    document.body.removeChild(a);
  }

}
