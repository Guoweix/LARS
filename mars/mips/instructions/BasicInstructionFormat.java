package mars.mips.instructions;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * loongarch Basic Instruction Format
 * TWO_R_TYPE 0000000000000000000000 jjjjj ddddd
 * THREE_R_TYPE 00000000000000000    kkkkk jjjjj ddddd
 * FOUR_R_TYPE 000000000000 aaaaa kkkkk jjjjj ddddd
 * TOW_R_I8_TYPE 
 * TOW_R_I12_TYPE
 * TOW_R_I14_TYPE
 * TOW_R_I16_TYPE
 * ONE_R_I21_TYPE
 * I26_TYPE
 */
public class BasicInstructionFormat {
    public static final BasicInstructionFormat TWO_R_TYPE = new BasicInstructionFormat();
    public static final BasicInstructionFormat THREE_R_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat FOUR_R_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat TOW_R_I8_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat TOW_R_I12_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat TOW_R_I14_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat TOW_R_I16_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat ONE_R_I21_TYPE= new BasicInstructionFormat();
    public static final BasicInstructionFormat I26_TYPE= new BasicInstructionFormat();
    
    // private default constructor prevents objects of this class other than those above.
    private BasicInstructionFormat() {
    }
}
