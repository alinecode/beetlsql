package org.beetl.sql.core.engine;

import java.io.Reader;
import java.util.Map;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.StatementParser;
import org.beetl.core.statement.PlaceholderST;
import org.beetl.core.statement.Program;
import org.beetl.core.statement.Statement;
import org.beetl.core.statement.VarRef;
import org.beetl.sql.core.PlaceHolderListener;

public class SQLTemplateEngine extends DefaultTemplateEngine
{
    public Program createProgram(Resource resource, Reader reader, Map<Integer, String> textMap, String cr,
                    GroupTemplate gt)
    {
            Program program = super.createProgram(resource, reader, textMap, cr, gt);
            modifyStatemetn(resource,program,gt);
            
            return program;
    }
    private void modifyStatemetn(Resource resource,Program program,GroupTemplate gt){
            Statement[] sts = program.metaData.statements;
            StatementParser parser = new StatementParser(sts, gt, resource.getId());
            
            parser.addListener(PlaceholderST.class, new PlaceHolderListener());
            parser.addListener(VarRef.class, new PlaceHolderListener());
            
            parser.parse();
    }
    
    
}
