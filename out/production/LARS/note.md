# lannch过程
## 1 区别是gui还是cli

```java
    
         if (gui) {
            launchIDE();  
            // System.out.println("GUI");
         } 
         else { 
            //...
         }
 ```
    - gui则使用MarsLaunch.launchIDE()
    - cli则进入下一步

## 2 设置内存和寄存器
```java
     MemoryConfigurations.setCurrentConfiguration(MemoryConfigurations.getDefaultConfiguration());
```
内存实际为Globals.memory这个对象
寄存器则为 RegisterFile

 设置内存和寄存器主要分两步
### 设置配置文件
 MemoryConfigurations内部有三个staic的元素
```java
      private static ArrayList configurations = null;// MemoryConfiguration的数组
      private static MemoryConfiguration defaultConfiguration;//内部由名称，id，地址名称，地址 组成
      private static MemoryConfiguration currentConfiguration;
```
    - MemoryConfigurations.getDefaultConfiguration();通过buildConfigurationCollection();构建configurations，在configurations加入三条MemoryConfiguration，分别是SPIM默认配置，compact data段在地址0处，compact text段在地址0处    
    再设置  currentConfigurationw和defaultConfiguration为第一条配置
### 修改 Globals.memory和RegisterFile
    -  MemoryConfigurations.setCurrentConfiguration(...);用配置文件将内存和寄存器设置完成

## 3 选项处理
```java
if (parseCommandArgs(args)) {
    ...
}
```
    - 使用循环处理参数 在pa后将参数保留，传给mips汇编代码

## 4运行指令（运行mips代码）
```java
runCommand()
```
### 4.1多文件处理

如果没有p选项则为单文件，如果有p选项则通过FilenameFinder来构造filesToAssemble数组和找出mainFile
```java
 Globals.getSettings().setBooleanSettingNonPersistent(Settings.DELAYED_BRANCHING_ENABLED, delayedBranching);
            Globals.getSettings().setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, selfModifyingCode);


            File mainFile = new File((String) filenameList.get(0)).getAbsoluteFile();// 将第一个文件作为'main'文件
            ArrayList filesToAssemble;//这是最后要的结果
            if (assembleProject) { 
               filesToAssemble = FilenameFinder.getFilenameList(mainFile.getParent(), Globals.fileExtensions);
               if (filenameList.size() > 1) {
                  // Using "p" project option PLUS listing more than one filename on command line.
                  // Add the additional files, avoiding duplicates.
                  filenameList.remove(0); // first one has already been processed
                
                  ArrayList moreFilesToAssemble = FilenameFinder.getFilenameList(filenameList, FilenameFinder.MATCH_ALL_EXTENSIONS);
                  // Remove any duplicates then merge the two lists.

                    //每回从输入文件列表中取出一个文件，遍历它所在文件的所有文件，最后添加到 filesToAssemble
                  for (int index2 = 0; index2<moreFilesToAssemble.size(); index2++) {
                     for (int index1 = 0; index1<filesToAssemble.size(); index1++) {
                        if (filesToAssemble.get(index1).equals(moreFilesToAssemble.get(index2))) {
                           moreFilesToAssemble.remove(index2);
                           index2--; // adjust for left shift in moreFilesToAssemble...
                           break;    // break out of inner loop...
                        }
                     }
                  }
                  filesToAssemble.addAll(moreFilesToAssemble);
               }
            }
```
这里用到了FilenameFinder这个类，这个类是专门用来处理文件的

```java
   ArrayList MIPSprogramsToAssemble = 
                      code.prepareFilesForAssembly(filesToAssemble, mainFile.getAbsolutePath(), null);		
```
将多个文件转化为MIPSprogram的列表，每个MIPSprogram中初始化了sourceList和tokenList，这个同时code中的sourceList和tokenList为main文件初始化。如果存在异常处理文件则第一个处理，同时将对应的MIPSprogram放在列表首位，mainfile放在1的位置。
```java
public ArrayList prepareFilesForAssembly(ArrayList filenames, String leadFilename, String exceptionHandler) throws ProcessingException {
         ArrayList MIPSprogramsToAssemble = new ArrayList();
         int leadFilePosition = 0;
         if (exceptionHandler != null && exceptionHandler.length() > 0) {
            filenames.add(0, exceptionHandler);
            leadFilePosition = 1;
         }
         for (int i=0; i<filenames.size(); i++) {
            String filename = (String) filenames.get(i);  
            MIPSprogram preparee = (filename.equals(leadFilename)) ? this : new MIPSprogram();
            preparee.readSource(filename);//填充sourceList，将非空行添加到soureList中
            preparee.tokenize();//填充tokenList
         	// I want "this" MIPSprogram to be the first in the list...except for exception handler
            if (preparee == this && MIPSprogramsToAssemble.size()>0) {
               MIPSprogramsToAssemble.add(leadFilePosition,preparee);
            } 
            else {
               MIPSprogramsToAssemble.add(preparee);
            }
         }
         return MIPSprogramsToAssemble;
      }
   
```
### 4.2 tokenize
在刚刚的代码    preparee.tokenize();中初始化tokenizer ，然后调用tokenizer.tokenize(this);
```java 
   
       public ArrayList tokenize(MIPSprogram p) throws ProcessingException {
         sourceMIPSprogram = p;
         equivalents = new HashMap<String,String>(); // DPS 11-July-2012
         ArrayList tokenList = new ArrayList();
         //ArrayList source = p.getSourceList();
         ArrayList<SourceLine> source = processIncludes(p, new HashMap<String,String>()); // DPS 9-Jan-2013
         p.setSourceLineList(source);
         TokenList currentLineTokens;
         String sourceLine;
         for (int i=0; i<source.size(); i++) {
            sourceLine = source.get(i).getSource(); 
            currentLineTokens = this.tokenizeLine(i+1, sourceLine);
            tokenList.add(currentLineTokens); 
            // DPS 03-Jan-2013. Related to 11-July-2012. If source code substitution was made
         	// based on .eqv directive during tokenizing, the processed line, a String, is 
         	// not the same object as the original line.  Thus I can use != instead of !equals()
         	// This IF statement will replace original source with source modified by .eqv substitution.
         	// Not needed by assembler, but looks better in the Text Segment Display.
            if (sourceLine.length() > 0 && sourceLine != currentLineTokens.getProcessedLine()) {
               source.set(i,new SourceLine(currentLineTokens.getProcessedLine(),source.get(i).getMIPSprogram(), source.get(i).getLineNumber())); 
            } 
         }
         if (errors.errorsOccurred()) {
            throw new ProcessingException(errors);
         }
         return tokenList;
      }
```