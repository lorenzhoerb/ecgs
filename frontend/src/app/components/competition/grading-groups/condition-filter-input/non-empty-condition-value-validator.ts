import { FormControl } from '@angular/forms';

export const notEmptyValidator = (control: FormControl) => {
  if (control.value && control.value.value.trim().length === 0) {
    return { notEmpty: true };
  }
  return null;
};
