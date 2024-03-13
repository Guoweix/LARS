package mars.mips.instructions;

import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;

/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

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
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */

public class InstructionSet {
   private ArrayList instructionList;
   private ArrayList opcodeMatchMaps;
   private SyscallLoader syscallLoader;

   /**
    * Creates a new InstructionSet object.
    */
   public InstructionSet() {
      instructionList = new ArrayList();

   }

   /**
    * Retrieve the current instruction set.
    */
   public ArrayList getInstructionList() {
      return instructionList;

   }

   /**
    * Adds all instructions to the set. A given extended instruction may have
    * more than one Instruction object, depending on how many formats it can have.
    * 
    * @see Instruction
    * @see BasicInstruction
    * @see ExtendedInstruction
    */
   @SuppressWarnings("unchecked")
   public void populate() {
      /*
       * Here is where the parade begins. Every instruction is added to the set here.
       */

      // //////////////////////////////////// BASIC INSTRUCTIONS START HERE
      // ////////////////////////////////

      instructionList.add(
            new BasicInstruction("fuck $t1,$t2,$t3",
                  "Addition with overflow : set $t3 to ($t2 add $t1 add 2)",
                  BasicInstructionFormat.THREE_R_TYPE,
                  "00000000000000000 kkkkk jjjjj ddddd ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int t2 = RegisterFile.getValue(operands[1]);
                        int t1 = RegisterFile.getValue(operands[0]);

                        int sum = t1 + t2 + 2;
                        // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((t1 >= 0 && sum < 0)
                              || (t1 < 0 && sum >= 0)) {
                           throw new ProcessingException(statement,
                                 "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[2], sum);
                     }
                  }));

      instructionList.add(
            new BasicInstruction("test $t0,$t1,$t2,-100",
                  "test  a k j d ",
                  BasicInstructionFormat.THREE_R_TYPE,
                  "00000 00000 aaaaaaa bbbbb ccccc ddddd ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        System.out.println(operands[0]);
                        System.out.println(operands[1]);
                        System.out.println(operands[2]);
                        System.out.println(operands[3]);

                     }
                  }));

      instructionList.add(
            new BasicInstruction("add.w $t1,$t2,$t3",
                  "Addition without overflow : set $t1 to ($t2 add  $t3) no overflow",
                  BasicInstructionFormat.THREE_R_TYPE,
                  "00000000000100000 ccccc bbbbb aaaaa",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int t2 = RegisterFile.getValue(operands[2]);
                        int t1 = RegisterFile.getValue(operands[1]);

                        int sum = t1 + t2;
                        RegisterFile.updateRegister(operands[0], sum);
                     }
                  }));

      instructionList.add(
            new BasicInstruction("sub.w $t1,$t2,$t3",
                  "substruction without overflow : set $t1 to ($t2 sub  $t3) no overflow",
                  BasicInstructionFormat.THREE_R_TYPE,
                  "00000000000100010 ccccc bbbbb aaaaa",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int t2 = RegisterFile.getValue(operands[1]);
                        int t1 = RegisterFile.getValue(operands[2]);

                        int sub = t1 - t2;
                        RegisterFile.updateRegister(operands[0], sub);
                     }
                  }));

      instructionList.add(
            new BasicInstruction("addi.w $t1,$t2,-100",
                  "Addition without overflow : set $t1 to ($t2 add  $t3) no overflow",
                  BasicInstructionFormat.TOW_R_I12_TYPE,
                  "0000001001 cccccccccccc bbbbb aaaaa",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int t2 = operands[2];
                        int t1 = RegisterFile.getValue(operands[1]);

                        int sum = t1 + t2;
                        RegisterFile.updateRegister(operands[0], sum);
                     }
                  }));

      //////////// Branch ////////////////////////

      instructionList.add(
            new BasicInstruction("beq $t1,$t2,-100",
                  "Branch if equal : Branch to statement at label's address if $t1 and $t2 are equal",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "010110 cccccccccccccccc  aaaaa bbbbb",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("bne $t1,$t-100",
                  "Branch if not equal : Branch to statement at label's address if $t1 and $t2 are not equal",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "010111 cccccccccccccccc   aaaaa bbbbb",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("blt $t1,$t2,-100",
                  "Branch if less : Branch to statement at label's address if $t1 is less than $t2",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "011000 cccccccccccccccc aaaaa bbbbb ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) < RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("bge $t1,$t2,-100",
                  "Branch if greater : Branch to statement at label's address if $t1 is greater than $t2",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "011001 cccccccccccccccc aaaaa bbbbb ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) > RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("bltu $t1,$t2,-100",
                  "Branch if less or equal to $t2  : Branch to statement at label's address if $t1 is less than $t2",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "011010 cccccccccccccccc aaaaa bbbbb ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) <= RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("bgeu $t1,$t2,-100",
                  "Branch if greater  : Branch to statement at label's address if $t1 is greater than $t2",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "011011 cccccccccccccccc aaaaa bbbbb ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) >= RegisterFile.getValue(operands[1])) {
                           processBranch(operands[2]);
                        }
                     }
                  }));

      instructionList.add(
            new BasicInstruction("b -100",
                  "Branch unconditionally   : Branch to statement at label's address",
                  BasicInstructionFormat.I26_TYPE,
                  "010100 aaaaa aaaaa aaaaa aaaaa aaaaaa",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        processBranch(operands[0]);
                     }
                  }));

      instructionList.add(
            new BasicInstruction("bl -100",
                  "Branch if greater  : Branch to statement at label's address if $t1 is greater than $t2",
                  BasicInstructionFormat.I26_TYPE,
                  "010101  aaaaa aaaaa   aaaaa aaaaaa aaaaa ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        processBranch(operands[0]);
                        RegisterFile.updateRegister(1, RegisterFile.getProgramCounter() + 4);

                     }
                  }));

      instructionList.add(
            new BasicInstruction("jirl $t1,$t2,target",
                  "Jumps to the address of the immediate number plus register and link: Set $t1 to Program Counter (return address) then jump to statement at the address of the immediate number plus register ",
                  BasicInstructionFormat.TOW_R_I16_TYPE,
                  "010011 ccccc ccccc ccccc c bbbbb aaaaa ",
                  new SimulationCode() {
                     public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        RegisterFile.updateRegister(operands[0], RegisterFile.getProgramCounter() + 4);
                        processJump(operands[2]+(operands[3]<<2));

                     }
                  }));



      addPseudoInstructions();

      ////////////// GET AND CREATE LIST OF SYSCALL FUNCTION OBJECTS
      ////////////// ////////////////////
      syscallLoader = new SyscallLoader();
      syscallLoader.loadSyscalls();

      // Initialization step. Create token list for each instruction example. This is
      // used by parser to determine user program correct syntax.
      for (int i = 0; i < instructionList.size(); i++) {
         Instruction inst = (Instruction) instructionList.get(i);
         inst.createExampleTokenList();
      }

      HashMap maskMap = new HashMap();
      ArrayList matchMaps = new ArrayList();
      for (int i = 0; i < instructionList.size(); i++) {
         Object rawInstr = instructionList.get(i);
         if (rawInstr instanceof BasicInstruction) {
            BasicInstruction basic = (BasicInstruction) rawInstr;
            Integer mask = Integer.valueOf(basic.getOpcodeMask());
            Integer match = Integer.valueOf(basic.getOpcodeMatch());
            HashMap matchMap = (HashMap) maskMap.get(mask);
            if (matchMap == null) {
               matchMap = new HashMap();
               maskMap.put(mask, matchMap);
               matchMaps.add(new MatchMap(mask, matchMap));
            }
            matchMap.put(match, basic);
         }
      }
      Collections.sort(matchMaps);
      this.opcodeMatchMaps = matchMaps;
   }

   public BasicInstruction findByBinaryCode(int binaryInstr) {
      ArrayList matchMaps = this.opcodeMatchMaps;
      for (int i = 0; i < matchMaps.size(); i++) {
         MatchMap map = (MatchMap) matchMaps.get(i);
         BasicInstruction ret = map.find(binaryInstr);
         if (ret != null)
            return ret;
      }
      return null;
   }

   /*
    * METHOD TO ADD PSEUDO-INSTRUCTIONS
    */

   private void addPseudoInstructions() {
      InputStream is = null;
      BufferedReader in = null;
      try {
         // leading "/" prevents package name being prepended to filepath.
         is = this.getClass().getResourceAsStream("/PseudoOps.txt");
         in = new BufferedReader(new InputStreamReader(is));
      } catch (NullPointerException e) {
         System.out.println(
               "Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
         System.exit(0);
      }
      try {
         String line, pseudoOp, template, firstTemplate, token;
         String description;
         StringTokenizer tokenizer;
         while ((line = in.readLine()) != null) {
            // skip over: comment lines, empty lines, lines starting with blank.
            if (!line.startsWith("#") && !line.startsWith(" ")
                  && line.length() > 0) {
               description = "";
               tokenizer = new StringTokenizer(line, "\t");
               pseudoOp = tokenizer.nextToken();
               template = "";
               firstTemplate = null;
               while (tokenizer.hasMoreTokens()) {
                  token = tokenizer.nextToken();
                  if (token.startsWith("#")) {
                     // Optional description must be last token in the line.
                     description = token.substring(1);
                     break;
                  }
                  if (token.startsWith("COMPACT")) {
                     // has second template for Compact (16-bit) memory config -- added DPS 3 Aug
                     // 2009
                     firstTemplate = template;
                     template = "";
                     continue;
                  }
                  template = template + token;
                  if (tokenizer.hasMoreTokens()) {
                     template = template + "\n";
                  }
               }
               ExtendedInstruction inst = (firstTemplate == null)
                     ? new ExtendedInstruction(pseudoOp, template, description)
                     : new ExtendedInstruction(pseudoOp, firstTemplate, template, description);
               instructionList.add(inst);
               // if (firstTemplate != null) System.out.println("\npseudoOp:
               // "+pseudoOp+"\ndefault template:\n"+firstTemplate+"\ncompact
               // template:\n"+template);
            }
         }
         in.close();
      } catch (IOException ioe) {
         System.out.println(
               "Internal Error: MIPS pseudo-instructions could not be loaded.");
         System.exit(0);
      } catch (Exception ioe) {
         System.out.println(
               "Error: Invalid MIPS pseudo-instruction specification.");
         System.exit(0);
      }

   }

   /**
    * Given an operator mnemonic, will return the corresponding Instruction
    * object(s)
    * from the instruction set. Uses straight linear search technique.
    * 
    * @param name operator mnemonic (e.g. addi, sw,...)
    * @return list of corresponding Instruction object(s), or null if not found.
    */
   public ArrayList matchOperator(String name) {
      ArrayList matchingInstructions = null;
      // Linear search for now....
      for (int i = 0; i < instructionList.size(); i++) {
         if (((Instruction) instructionList.get(i)).getName().equalsIgnoreCase(name)) {
            if (matchingInstructions == null)
               matchingInstructions = new ArrayList();
            matchingInstructions.add(instructionList.get(i));
         }
      }
      return matchingInstructions;
   }

   /**
    * Given a string, will return the Instruction object(s) from the instruction
    * set whose operator mnemonic prefix matches it. Case-insensitive. For example
    * "s" will match "sw", "sh", "sb", etc. Uses straight linear search technique.
    * 
    * @param name a string
    * @return list of matching Instruction object(s), or null if none match.
    */
   public ArrayList prefixMatchOperator(String name) {
      ArrayList matchingInstructions = null;
      // Linear search for now....
      if (name != null) {
         for (int i = 0; i < instructionList.size(); i++) {
            if (((Instruction) instructionList.get(i)).getName().toLowerCase().startsWith(name.toLowerCase())) {
               if (matchingInstructions == null)
                  matchingInstructions = new ArrayList();
               matchingInstructions.add(instructionList.get(i));
            }
         }
      }
      return matchingInstructions;
   }

   /*
    * Method to find and invoke a syscall given its service number. Each syscall
    * function is represented by an object in an array list. Each object is of
    * a class that implements Syscall or extends AbstractSyscall.
    */

   private void findAndSimulateSyscall(int number, ProgramStatement statement)
         throws ProcessingException {
      Syscall service = syscallLoader.findSyscall(number);
      if (service != null) {
         service.simulate(statement);
         return;
      }
      throw new ProcessingException(statement,
            "invalid or unimplemented syscall service: " +
                  number + " ",
            Exceptions.SYSCALL_EXCEPTION);
   }

   /*
    * Method to process a successful branch condition. DO NOT USE WITH JUMP
    * INSTRUCTIONS! The branch operand is a relative displacement in words
    * whereas the jump operand is an absolute address in bytes.
    *
    * The parameter is displacement operand from instruction.
    *
    * Handles delayed branching if that setting is enabled.
    */
   // 4 January 2008 DPS: The subtraction of 4 bytes (instruction length) after
   // the shift has been removed. It is left in as commented-out code below.
   // This has the effect of always branching as if delayed branching is enabled,
   // even if it isn't. This mod must work in conjunction with
   // ProgramStatement.java, buildBasicStatementFromBasicInstruction() method near
   // the bottom (currently line 194, heavily commented).

   // @SuppressWarnings("deprecation")
   private void processBranch(int displacement) {
      // if (Globals.getSettings().getDelayedBranchingEnabled()) {
      // Register the branch target address (absolute byte address).
      // DelayedBranch.register(RegisterFile.getProgramCounter() + (displacement <<
      // 2));
      // } else {
      // Decrement needed because PC has already been incremented
      RegisterFile.setProgramCounter(
            RegisterFile.getProgramCounter()
                  + (displacement << 2)); // - Instruction.INSTRUCTION_LENGTH);
      // }
   }

   /*
    * Method to process a jump. DO NOT USE WITH BRANCH INSTRUCTIONS!
    * The branch operand is a relative displacement in words
    * whereas the jump operand is an absolute address in bytes.
    *
    * The parameter is jump target absolute byte address.
    *
    * Handles delayed branching if that setting is enabled.
    */

   private void processJump(int targetAddress) {
      // if (Globals.getSettings().getDelayedBranchingEnabled()) {
         // DelayedBranch.register(targetAddress);
      // } else {
         RegisterFile.setProgramCounter(targetAddress);
      // }
   }

   /*
    * Method to process storing of a return address in the given
    * register. This is used only by the "and link"
    * instructions: jal, jalr, bltzal, bgezal. If delayed branching
    * setting is off, the return address is the address of the
    * next instruction (e.g. the current PC value). If on, the
    * return address is the instruction following that, to skip over
    * the delay slot.
    *
    * The parameter is register number to receive the return address.
    */

   private void processReturnAddress(int register) {
      RegisterFile.updateRegister(register, RegisterFile.getProgramCounter() +
            ((Globals.getSettings().getDelayedBranchingEnabled()) ? Instruction.INSTRUCTION_LENGTH : 0));
   }

   private static class MatchMap implements Comparable {
      private int mask;
      private int maskLength; // number of 1 bits in mask
      private HashMap matchMap;

      public MatchMap(int mask, HashMap matchMap) {
         this.mask = mask;
         this.matchMap = matchMap;

         int k = 0;
         int n = mask;
         while (n != 0) {
            k++;
            n &= n - 1;
         }
         this.maskLength = k;
      }

      public boolean equals(Object o) {
         return o instanceof MatchMap && mask == ((MatchMap) o).mask;
      }

      public int compareTo(Object other) {
         MatchMap o = (MatchMap) other;
         int d = o.maskLength - this.maskLength;
         if (d == 0)
            d = this.mask - o.mask;
         return d;
      }

      public BasicInstruction find(int instr) {
         int match = Integer.valueOf(instr & mask);
         return (BasicInstruction) matchMap.get(match);
      }
   }
}